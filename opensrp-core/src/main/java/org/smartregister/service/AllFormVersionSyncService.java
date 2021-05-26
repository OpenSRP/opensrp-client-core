package org.smartregister.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.DristhiConfiguration;
import org.smartregister.domain.DownloadStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.FormDefinitionVersion;
import org.smartregister.domain.Response;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.FormsVersionRepository;
import org.smartregister.util.ZipUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.util.Log.logError;

/**
 * Created by Dimas Ciputra on 3/23/15.
 */
public class AllFormVersionSyncService {

    private static final String FORM_DEF_VERSION_FIELD = "form_data_definition_version";
    private static final String FORM_DEF_JSON_FILENAME = "form_definition.json";
    private final HTTPAgent httpAgent;
    private final DristhiConfiguration configuration;
    private final FormsVersionRepository formsVersionRepository;

    public AllFormVersionSyncService(HTTPAgent httpAgentArg, DristhiConfiguration
            configurationArg, FormsVersionRepository formsVersionRepositoryArg) {
        formsVersionRepository = formsVersionRepositoryArg;
        httpAgent = httpAgentArg;
        configuration = configurationArg;
    }

    public FetchStatus pullFormDefinitionFromServer() {
        FetchStatus status = FetchStatus.nothingFetched;
        String baseUrl = configuration.dristhiBaseURL();
        String uri = baseUrl + AllConstants.ALL_FORM_VERSION_URL;

        Response<String> response = httpAgent.fetch(uri);
        if (response.isFailure()) {
            logError("Form definition pull error");
            status = FetchStatus.fetchedFailed;
            return status;
        }

        String formVersions;

        try {
            JSONObject jsonObject = new JSONObject(response.payload());
            formVersions = jsonObject.get("formVersions").toString();
        } catch (JSONException e) {
            return status;
        }

        List<FormDefinitionVersion> forms = new Gson()
                .fromJson(formVersions, new TypeToken<List<FormDefinitionVersion>>() {
                }.getType());

        if (forms.size() > 0) {
            for (FormDefinitionVersion form : forms) {
                try {
                    if (!formsVersionRepository.formExists(form.getFormDirName())) {
                        /* Form not exist yet, add it to repository */
                        form.setSyncStatus(SyncStatus.PENDING);
                        formsVersionRepository.addFormVersionFromObject(form);
                    } else {
                        /* Form is exist, update it */
                        FormDefinitionVersion formDefinitionVersion = formsVersionRepository
                                .getFormByFormDirName(form.getFormDirName());

                        /* If form name is not equal, then update it */
                        if (!formDefinitionVersion.getFormName().equals(form.getFormName())) {
                            formsVersionRepository
                                    .updateFormName(form.getFormDirName(), form.getFormName());
                        }

                        int repoVersion = Integer.parseInt(formDefinitionVersion.getVersion());
                        int pulledVersion = Integer.parseInt(form.getVersion());

                        if (pulledVersion > repoVersion) {
                            formsVersionRepository
                                    .updateServerVersion(form.getFormDirName(), form.getVersion());
                            formsVersionRepository
                                    .updateSyncStatus(form.getFormDirName(), SyncStatus.PENDING);
                            status = FetchStatus.fetched;
                        }
                    }
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }

        return status;
    }

    public DownloadStatus downloadAllPendingFormFromServer() {
        DownloadStatus status = DownloadStatus.nothingDownloaded;
        List<FormDefinitionVersion> pendingFormList = formsVersionRepository.
                getAllFormWithSyncStatus(SyncStatus.PENDING);

        if (pendingFormList.isEmpty()) {
            return status;
        } else {
            for (FormDefinitionVersion l : pendingFormList) {
                String downloadLink =
                        configuration.dristhiBaseURL() + AllConstants.FORM_DOWNLOAD_URL + l
                                .getFormDirName();

                status = httpAgent.downloadFromUrl(downloadLink, l.getFormDirName() + ".zip");
                if (status == DownloadStatus.downloaded) {
                    formsVersionRepository.updateSyncStatus(l.getFormDirName(), SyncStatus.SYNCED);
                }
            }
        }
        return status;
    }

    /* Unzip all downloaded form files */
    public void unzipAllDownloadedFormFile() {
        File dir = new File(FormPathService.sdcardPathDownload);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().matches(".*\\.zip");
            }
        };

        File[] zipFiles = dir.listFiles(filter);

        for (File f : zipFiles) {
            ZipUtil zipUtil = new ZipUtil(f.getAbsolutePath(),
                    FormPathService.sdcardPath + f.getName().replaceAll(".zip", "")
                            + File.separator);
            zipUtil.unzip();
        }
    }

    /* Verify all forms file in sdcard */
    public void verifyFormsInFolder() {

        File[] formFiles = listFormFiles();

        if (formFiles == null) {
            formsVersionRepository.deleteAll();
            return;
        }

        List<File> formStoragelist = new LinkedList<File>(Arrays.asList(formFiles));
        List<Map<String, String>> formRepoList = formsVersionRepository
                .getAllFormWithSyncStatusAsMap(SyncStatus.SYNCED);

        /* verify file in repo */
        if (!formRepoList.isEmpty()) {
            for (Map<String, String> form : formRepoList) {
                boolean formFound = false;
                for (File f : formStoragelist) {
                    if (form.containsValue(f.getName())) {
                        formFound = true;
                        formStoragelist.remove(f);
                        break;
                    }
                }
                if (!formFound) {
                    formsVersionRepository
                            .updateSyncStatus(form.get(FormsVersionRepository.FORM_DIR_NAME_COLUMN),
                                    SyncStatus.PENDING);
                }
            }
        }
        /* verify file in storage */
        for (File f : formStoragelist) {
            if (!formsVersionRepository.formExists(f.getName())) {
                /* Add to Repository */
                try {
                    FormDefinitionVersion form = getFormDefinitionFromFile(f);
                    formsVersionRepository.addFormVersionFromObject(form);
                } catch (Exception ex) {
                    Timber.e(ex);
                }
            }
        }
    }

    protected FormDefinitionVersion getFormDefinitionFromFile(File f) {
        String version = null;

        try {
            FileInputStream fln = new FileInputStream(
                    new File(f.getAbsolutePath() + File.separator + FORM_DEF_JSON_FILENAME));
            String jsonString = null;
            try {
                FileChannel fc = fln.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                jsonString = Charset.defaultCharset().decode(bb).toString();
            } finally {
                fln.close();
            }
            JSONObject jsonObject = new JSONObject(jsonString);
            version = (String) jsonObject.get(FORM_DEF_VERSION_FIELD);
        } catch (Exception e) {
            Timber.e(e);
        }

        return new FormDefinitionVersion(f.getName(), f.getName(), version)
                .withSyncStatus(SyncStatus.SYNCED);
    }

    protected File[] listFormFiles() {
        File dir = new File(FormPathService.sdcardPath);
        return dir.listFiles();
    }

}
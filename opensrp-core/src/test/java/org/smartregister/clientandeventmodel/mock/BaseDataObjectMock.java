package org.smartregister.clientandeventmodel.mock;

import org.smartregister.clientandeventmodel.BaseDataObject;
import org.smartregister.clientandeventmodel.User;

import java.util.Date;

/**
 * Created by kaderchowdhury on 20/11/17.
 */
public class BaseDataObjectMock extends BaseDataObject {

    @Override
    public User getCreator() {
        return super.getCreator();
    }

    @Override
    public void setCreator(User creator) {
        super.setCreator(creator);
    }

    @Override
    public Date getDateCreated() {
        return super.getDateCreated();
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        super.setDateCreated(dateCreated);
    }

    @Override
    public User getEditor() {
        return super.getEditor();
    }

    @Override
    public void setEditor(User editor) {
        super.setEditor(editor);
    }

    @Override
    public Date getDateEdited() {
        return super.getDateEdited();
    }

    @Override
    public void setDateEdited(Date dateEdited) {
        super.setDateEdited(dateEdited);
    }

    @Override
    public Boolean getVoided() {
        return super.getVoided();
    }

    @Override
    public void setVoided(Boolean voided) {
        super.setVoided(voided);
    }

    @Override
    public Date getDateVoided() {
        return super.getDateVoided();
    }

    @Override
    public void setDateVoided(Date dateVoided) {
        super.setDateVoided(dateVoided);
    }

    @Override
    public User getVoider() {
        return super.getVoider();
    }

    @Override
    public void setVoider(User voider) {
        super.setVoider(voider);
    }

    @Override
    public String getVoidReason() {
        return super.getVoidReason();
    }

    @Override
    public void setVoidReason(String voidReason) {
        super.setVoidReason(voidReason);
    }

    @Override
    public BaseDataObject withCreator(User creator) {
        return super.withCreator(creator);
    }

    @Override
    public BaseDataObject withDateCreated(Date dateCreated) {
        return super.withDateCreated(dateCreated);
    }

    @Override
    public BaseDataObject withEditor(User editor) {
        return super.withEditor(editor);
    }

    @Override
    public BaseDataObject withDateEdited(Date dateEdited) {
        return super.withDateEdited(dateEdited);
    }

    @Override
    public BaseDataObject withVoided(Boolean voided) {
        return super.withVoided(voided);
    }

    @Override
    public BaseDataObject withDateVoided(Date dateVoided) {
        return super.withDateVoided(dateVoided);
    }

    @Override
    public BaseDataObject withVoider(User voider) {
        return super.withVoider(voider);
    }

    @Override
    public BaseDataObject withVoidReason(String voidReason) {
        return super.withVoidReason(voidReason);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

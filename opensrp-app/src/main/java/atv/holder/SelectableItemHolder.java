package atv.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;

import org.smartregister.R;

import atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/15/15.
 */
public class SelectableItemHolder extends TreeNode.BaseNodeViewHolder<String> {
    private TextView tvValue;
    private CheckBox nodeSelector;
    private TextView levelValue;
    private String level;
    private com.github.johnkil.print.PrintView arrowView;
    private Context context;

    public SelectableItemHolder(Context context, String Level) {
        super(context);
        level = Level;
        this.context = context;
    }

    @Override
    public View createNodeView(final TreeNode node, String value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_selectable_item, null, false);

        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(level + " " + value);
        arrowView = (com.github.johnkil.print.PrintView) view.findViewById(R.id.arrowview);
//        levelValue = (TextView) view.findViewById(R.id.treenodetext);
//        levelValue.setText("\\u2605");

        nodeSelector = (CheckBox) view.findViewById(R.id.node_selector);
        nodeSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                node.setSelected(isChecked);
                if (isChecked) {
                    node.setExpanded(isChecked);
                }
            }
        });
        nodeSelector.setChecked(node.isSelected());
        view.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.bot_line).setVisibility(View.INVISIBLE);
        if (node.isLeaf()) {
            ((PrintView) view.findViewById(R.id.arrowview))
                    .setIconText(view.getContext().getString(R.string.ic_check_circle_blank));
        }
//        if(node.isFirstChild()){
//            view.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
//            if(node.isExpanded()){
//                view.findViewById(R.id.bot_line).setVisibility(View.VISIBLE);
//            }
//        }

        if (node.isFirstChild()) {
            view.findViewById(R.id.top_line).setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        nodeSelector.setVisibility(editModeEnabled ? View.VISIBLE : View.GONE);
        nodeSelector.setChecked(mNode.isSelected());
    }

    @Override
    public void toggle(boolean active) {
        if (!mNode.isLeaf()) {
            arrowView.setIconText(context.getResources().getString(
                    active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
        }
    }
}

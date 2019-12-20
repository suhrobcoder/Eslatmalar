package uz.suhrob.eslatmalar;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by User on 14.12.2019.
 */

public class ActionBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG = "ActionBottomDialog";

    private ItemClickListener mListener;

    public static ActionBottomSheetDialogFragment newInstance() {
        return new ActionBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        view.findViewById(R.id.everyday_type).setOnClickListener(this);
        view.findViewById(R.id.week_type).setOnClickListener(this);
        view.findViewById(R.id.once_type).setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        TextView tvSelected = (TextView)view;
        mListener.onItemClick(tvSelected.getText().toString());
        dismiss();
    }

    public interface ItemClickListener {
        void onItemClick(String item);
    }
}

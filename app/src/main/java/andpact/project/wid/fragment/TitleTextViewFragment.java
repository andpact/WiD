package andpact.project.wid.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import andpact.project.wid.R;

public class TitleTextViewFragment extends Fragment {
    private static final String ARG_TEXT = "text";

    public static TitleTextViewFragment newInstance(int position) {
        TitleTextViewFragment titleTextViewFragment = new TitleTextViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEXT, position);
        titleTextViewFragment.setArguments(args);
        return titleTextViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_title_text_view, container, false);

        // 텍스트 뷰에 해당하는 작업 수행
        TextView textView = view.findViewById(R.id.textView);
        int position = getArguments().getInt(ARG_TEXT);
        textView.setText(getTextForPosition(position));

        return view;
    }

    private String getTextForPosition(int position) {
        // 각각의 위치에 해당하는 텍스트를 반환
        String[] texts = {"공부", "일", "독서", "운동", "잠", "이동", "취미", "기타"};
        if (position >= 0 && position < texts.length) {
            return texts[position];
        }
        return "";
    }
}
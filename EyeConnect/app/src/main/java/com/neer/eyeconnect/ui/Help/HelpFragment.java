package com.neer.eyeconnect.ui.Help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.neer.eyeconnect.R;

public class HelpFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_help, container, false);

        TextView que1 = root.findViewById(R.id.que1);
        TextView ans1 = root.findViewById(R.id.ans1);
        TextView que2 = root.findViewById(R.id.que2);
        TextView ans2 = root.findViewById(R.id.ans2);
        TextView que3 = root.findViewById(R.id.que3);
        TextView ans3 = root.findViewById(R.id.ans3);
        TextView que4 = root.findViewById(R.id.que4);
        TextView ans4 = root.findViewById(R.id.ans4);

        CardView card1=root.findViewById(R.id.card1);
        CardView card2=root.findViewById(R.id.card2);
        CardView card3=root.findViewById(R.id.card3);
        CardView card4=root.findViewById(R.id.card4);

        // Set click listeners for que1 and que2
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(ans1);
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(ans2);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(ans3);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleVisibility(ans4);
            }
        });

        return root;
    }

    private void toggleVisibility(TextView textView) {
        int currentVisibility = textView.getVisibility();
        textView.setVisibility(currentVisibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}

package cn.edu.hnit.schedule.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class MyDialog extends Dialog {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public MyDialog(Context context, View layout) {
        super(context);
        setContentView(layout);
    }

}

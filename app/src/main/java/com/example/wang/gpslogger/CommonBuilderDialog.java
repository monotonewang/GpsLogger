package com.example.wang.gpslogger;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * Created by Administrator on 2017/12/18 0018.
 */

public class CommonBuilderDialog extends AppCompatDialog {

    public CommonBuilderDialog(Context context) {
//        super(context,1);
        super(context);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0x0));
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.dimAmount = (float) 0.4;
            layoutParams.gravity = Gravity.CENTER;
            //TODO
//            layoutParams.windowAnimations = R.style.dialog_center;
            window.setAttributes(layoutParams);
        }
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    public TextView getTvMessage(){
//        return (TextView) findViewById(R.id.tv_info);
//    }
//
//    public TextView getTvPositive(){
//        return (TextView) findViewById(R.id.tv_sure);
//    }
//
//    public TextView getTvNegative(){
//        return (TextView) findViewById(R.id.tv_cancel);
//    }
//
//    public ImageView getIvIcon(){
//        return (ImageView) findViewById(R.id.iv_icon);
//    }

    public String getMessage() {

        TextView tvInfo = (TextView) findViewById(R.id.tv_info);
        if (tvInfo == null) {
            return null;
        } else {
            return (String) tvInfo.getText();
        }
    }

    public static class Builder {

        ImageView ivIcon;
        TextView tvNegative;
        TextView tvPositive;
        TextView tvInfo;
        View viewMiddle;
        ProgressBar progressBar;
        private Context context;
        CommonBuilderDialog dialog;
        boolean isOne;
        boolean isShow;
        boolean isBottomEmpty;
        boolean isAlert;
        String positiveTitle;
        String negativeTitle;
        String message;
        boolean isCancle;
        String sureText;

        OnClickListener sureListener;
        OnClickListener negativeButtonListener;

        public Builder(Context context) {
            this.context = context;
        }

        public CommonBuilderDialog create() {
            dialog = new CommonBuilderDialog(context);
            dialog.setContentView(R.layout.dialog_custom);

            ivIcon = dialog.findViewById(R.id.iv_icon);
            tvNegative = dialog.findViewById(R.id.tv_cancel);
            tvPositive = dialog.findViewById(R.id.tv_sure);
            tvInfo = dialog.findViewById(R.id.tv_info);
            viewMiddle = dialog.findViewById(R.id.v_middle);
            progressBar = dialog.findViewById(R.id.progress);

            tvNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeButtonListener != null) {
                        negativeButtonListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    }
                    dialog.dismiss();
                }
            });

            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sureListener != null)
                        sureListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                }
            });

            if (isOne) {
                tvNegative.setVisibility(View.GONE);
                viewMiddle.setVisibility(View.GONE);
            } else {
                tvNegative.setVisibility(View.VISIBLE);
                viewMiddle.setVisibility(View.VISIBLE);
            }

            tvInfo.setText(message);

            ivIcon.setImageResource(isAlert ? R.drawable.prompt : R.drawable.password_success);

            tvPositive.setText(positiveTitle);
            tvNegative.setText(negativeTitle);
            dialog.setCancelable(false);
//            ButterKnife.bind(viewRoot);
            return dialog;
        }


        public ProgressBar getProgressBar() {
            return progressBar;
        }

        /**
         * 底部是否显示一个
         *
         * @param isOne
         * @return
         */
        public Builder setIsOne(boolean isOne) {
            this.isOne = isOne;
            if (tvNegative != null && viewMiddle != null) {
                if (isOne) {
                    tvNegative.setVisibility(View.GONE);
                    viewMiddle.setVisibility(View.GONE);
                } else {
                    tvNegative.setVisibility(View.VISIBLE);
                    viewMiddle.setVisibility(View.VISIBLE);
                }
            }
            return this;
        }

        public Builder showProgressBar(boolean isShow) {
            this.isShow = isShow;

            if (dialog == null) {
                System.out.println("----------------------------------------------------dialog is null");
            }
            if (progressBar == null) {
                System.out.println("----------------------------------------------------progress is null");
            }


            if (dialog != null && progressBar != null) {
                progressBar.setVisibility(isShow ? View.VISIBLE : View.GONE);
            } else {


//                System.out.println(" dialog is null or progress is null");
            }
            return this;
        }

        public boolean isProgressBar() {

            if (dialog != null && progressBar != null) {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    return true;
                }
                return false;
            } else {
                return false;

//                System.out.println(" dialog is null or progress is null");
            }
        }

        public Builder setIvIcon(boolean isAlert) {
            this.isAlert = isAlert;
            return this;
        }

        public Builder setPositiveButton(String title, OnClickListener onClickListener) {
            this.sureListener = onClickListener;
            this.positiveTitle = title;
            if (dialog != null && tvPositive != null) {
                tvPositive.setText(title);
            }
            return this;
        }

        public Builder setNegativeButton(String negativeTitle, OnClickListener onClickListener) {
            this.negativeButtonListener = onClickListener;
            this.negativeTitle = negativeTitle;
            if (dialog != null && tvNegative != null) {
                tvNegative.setText(negativeTitle);
            }
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            if (dialog != null && tvInfo != null) {
                tvInfo.setText(message);
            }
            return this;
        }


        public CommonBuilderDialog show() {
            final CommonBuilderDialog dialog = create();
            dialog.show();
            return dialog;
        }


        public Builder setCancelable(boolean isCancle) {
            this.isCancle = isCancle;
            if (dialog != null) {
                dialog.setCancelable(isCancle);
            }
            return this;
        }

        //TODO
        public Builder setPrompt(boolean isAlert) {
            this.isAlert = isAlert;
            if (dialog != null && ivIcon != null) {
                ivIcon.setImageResource(isAlert ? R.drawable.prompt : R.drawable.password_success);
            }
            return this;
        }

        public Builder setOneText(String sureText) {
            this.sureText = sureText;

            if (dialog != null && ivIcon != null) {
                tvPositive.setText(sureText);
            }
            return this;
        }
    }


    @Override
    public void onBackPressed() {

    }
}

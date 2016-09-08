package com.tbd.androidshowcase.utility;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import java.util.Set;

/**
 * Created by Trevor on 9/7/2016.
 */
public class DemoNoSQLNotesResult implements DemoNoSQLResult {
    private static final int KEY_TEXT_COLOR = 0xFF333333;
    private final NotesDO result;

    DemoNoSQLNotesResult(final NotesDO result) {
        this.result = result;
    }
    @Override
    public void updateItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        final String originalValue = result.getContent();
        result.setContent(DemoSampleDataGenerator.getRandomSampleString("content"));
        try {
            mapper.save(result);
        } catch (final AmazonClientException ex) {
            // Restore original data if save fails, and re-throw.
            result.setContent(originalValue);
            throw ex;
        }
    }

    @Override
    public void deleteItem() {
        final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
        mapper.delete(result);
    }

    private void setKeyTextViewStyle(final TextView textView) {
        textView.setTextColor(KEY_TEXT_COLOR);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(5), dp(2), dp(5), 0);
        textView.setLayoutParams(layoutParams);
    }

    /**
     * @param dp number of design pixels.
     * @return number of pixels corresponding to the desired design pixels.
     */
    private int dp(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private void setValueTextViewStyle(final TextView textView) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(dp(15), 0, dp(15), dp(2));
        textView.setLayoutParams(layoutParams);
    }

    private void setKeyAndValueTextViewStyles(final TextView keyTextView, final TextView valueTextView) {
        setKeyTextViewStyle(keyTextView);
        setValueTextViewStyle(valueTextView);
    }

    private static String bytesToHexString(byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        builder.append(String.format("%02X", bytes[0]));
        for(int index = 1; index < bytes.length; index++) {
            builder.append(String.format(" %02X", bytes[index]));
        }
        return builder.toString();
    }

    private static String byteSetsToHexStrings(Set<byte[]> bytesSet) {
        final StringBuilder builder = new StringBuilder();
        int index = 0;
        for (byte[] bytes : bytesSet) {
            builder.append(String.format("%d: ", ++index));
            builder.append(bytesToHexString(bytes));
            if (index < bytesSet.size()) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    @Override
    public View getView(final Context context, final View convertView, int position) {
        final LinearLayout layout;
        final TextView resultNumberTextView;
        final TextView userIdKeyTextView;
        final TextView userIdValueTextView;
        final TextView noteIdKeyTextView;
        final TextView noteIdValueTextView;
        final TextView contentKeyTextView;
        final TextView contentValueTextView;
        final TextView creationDateKeyTextView;
        final TextView creationDateValueTextView;
        final TextView titleKeyTextView;
        final TextView titleValueTextView;
        if (convertView == null) {
            layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            resultNumberTextView = new TextView(context);
            resultNumberTextView.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(resultNumberTextView);


            userIdKeyTextView = new TextView(context);
            userIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(userIdKeyTextView, userIdValueTextView);
            layout.addView(userIdKeyTextView);
            layout.addView(userIdValueTextView);

            noteIdKeyTextView = new TextView(context);
            noteIdValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(noteIdKeyTextView, noteIdValueTextView);
            layout.addView(noteIdKeyTextView);
            layout.addView(noteIdValueTextView);

            contentKeyTextView = new TextView(context);
            contentValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(contentKeyTextView, contentValueTextView);
            layout.addView(contentKeyTextView);
            layout.addView(contentValueTextView);

            creationDateKeyTextView = new TextView(context);
            creationDateValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(creationDateKeyTextView, creationDateValueTextView);
            layout.addView(creationDateKeyTextView);
            layout.addView(creationDateValueTextView);

            titleKeyTextView = new TextView(context);
            titleValueTextView = new TextView(context);
            setKeyAndValueTextViewStyles(titleKeyTextView, titleValueTextView);
            layout.addView(titleKeyTextView);
            layout.addView(titleValueTextView);
        } else {
            layout = (LinearLayout) convertView;
            resultNumberTextView = (TextView) layout.getChildAt(0);

            userIdKeyTextView = (TextView) layout.getChildAt(1);
            userIdValueTextView = (TextView) layout.getChildAt(2);

            noteIdKeyTextView = (TextView) layout.getChildAt(3);
            noteIdValueTextView = (TextView) layout.getChildAt(4);

            contentKeyTextView = (TextView) layout.getChildAt(5);
            contentValueTextView = (TextView) layout.getChildAt(6);

            creationDateKeyTextView = (TextView) layout.getChildAt(7);
            creationDateValueTextView = (TextView) layout.getChildAt(8);

            titleKeyTextView = (TextView) layout.getChildAt(9);
            titleValueTextView = (TextView) layout.getChildAt(10);
        }

        resultNumberTextView.setText(String.format("#%d", + position+1));
        userIdKeyTextView.setText("userId");
        userIdValueTextView.setText(result.getUserId());
        noteIdKeyTextView.setText("noteId");
        noteIdValueTextView.setText(result.getNoteId());
        contentKeyTextView.setText("content");
        contentValueTextView.setText(result.getContent());
        creationDateKeyTextView.setText("creationDate");
        creationDateValueTextView.setText("" + result.getCreationDate().longValue());
        titleKeyTextView.setText("title");
        titleValueTextView.setText(result.getTitle());
        return layout;
    }
}
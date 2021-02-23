package com.first.orient.base.widget;


import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模仿抖音的#Topic功能
 *
 * Created by HarryJoker on 2020/4/25.
 */

public class TopicEditText extends AppCompatEditText {

    private StringBuilder lastText;

    private List<String> topics;

    private int spanColor = Color.BLUE;

    private int textColor = Color.BLACK;

    // 正则表达式
   private static final String reg = "#([a-zA-Z0-9]*)";

    public TopicEditText(Context context) {
        super(context);
    }

    public TopicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TopicEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (lastText == null) lastText = new StringBuilder();
        if (text == null || text.toString().equals(lastText.toString())) return;
        lastText.setLength(0);
        lastText.append(text);
        makeSpanTopic(text, start, lengthBefore, lengthAfter);
    }

    private void makeSpanTopic(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        Spannable sps = new SpannableString(text.toString());
        if (topics != null)  topics.clear();
        if (text.toString().contains("#")) {
            //修改上方正则表达式和测试内容点击运行就可以在线进行测试， 也可以修改下方代码
            Matcher matcher = Pattern.compile(reg).matcher(text.toString());
            if (topics == null) topics = new ArrayList<>();
            while (matcher.find()) {
                String topic = matcher.group();
                if (topic.length() > 1) {
                    int startTopic = text.toString().indexOf(topic);
                    makeSpan(sps, startTopic, startTopic + topic.length());
                    topics.add(topic.replace("#", ""));
                }
            }
        }
        setText(sps);
        setSelection(start + lengthAfter);
        Log.d("Topics", topics == null ? "" : Arrays.deepToString(topics.toArray()));
    }

    //生成一个需要整体删除的Span
    private void makeSpan(Spannable sps, int start, int end) {
        sps.setSpan(new ForegroundColorSpan(spanColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public List<String> getTopics() {
        return topics;
    }

    public String getStringTopics() {
        if (topics == null) return "";
        StringBuilder builder = new StringBuilder();
        for (String topic : topics) {
            builder.append(builder.length() > 0 ? "," : "");
            builder.append(topic);
        }
        return builder.toString();
    }

    public void setSpanColor(int spanColor) {
        this.spanColor = spanColor;
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getSpanColor() {
        return spanColor;
    }

    public int getTextColor() {
        return textColor;
    }
}

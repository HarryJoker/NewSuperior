package gov.android.com.superior.ui.unit.dashboard.trace;

//public class TraceDoneActivity extends BaseToolBarActivity {
//
//
//    private int unitTaskId;
//
//    private EditText etContent;
//
//    @Override
//    public void onInitParams() {
//        unitTaskId = getIntent().getIntExtra("unitTaskId", 0);
//
//        if (unitTaskId == 0) {
//            Toast.makeText(this, "数据错误了，请重试", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//    }
//
//    @Override
//    public int getLayoutRes() {
//        return R.layout.activity_trace_done;
//    }
//
//    @Override
//    protected void onBarTitle(TextView barTitle) {
//        barTitle.setText("调度完成审核");
//    }
//
//    @Override
//    protected void onFindViews() {
//        etContent = findViewById(R.id.et_content);
//    }
//
//    @Override
//    public void onInitView() {
//
//    }
//
//    @Override
//    public void onInitPresenter() {
//
//    }
//
//    @Override
//    public void onBusiness() {
//
//    }
//
//
//    private boolean checked() {
//        if (TextUtils.isEmpty(etContent.getEditableText().toString())) {
//            Toast.makeText(this, "请填写调度完成描述", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    public void veriryTraceClick(View v) {
//        if (checked()) {
//            newVerifyTrace();
//        }
//    }
//
//    private void newVerifyTrace() {
//        showLoading("审核中...");
//        HttpParams params = new HttpParams();
//        params.put("unitTaskId", unitTaskId);
//        params.put("content", etContent.getText().toString());
//        params.put("userId", User.getInstance().getUserId());
//        params.put("status", 91);//已报送
//        params.put("progress", 100);
//        params.put("unitId", User.getInstance().getUnitId());
//        OkGo.<JSONObject>post(HttpUrl.NEW_DONE_TRACE).params(params).tag(this).execute(getJsonObjectCallback(HttpUrl.NEW_DONE_TRACE));
//    }
//
//    @Override
//    protected void onJsonObjectCallBack(String action, JSONObject data) {
//        super.onJsonObjectCallBack(action, data);
//        Toast.makeText(this, "已调度完成", Toast.LENGTH_SHORT).show();
//        setResult(RESULT_OK);
//        finish();
//    }
//}

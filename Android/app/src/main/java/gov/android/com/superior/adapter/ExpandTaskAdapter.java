package gov.android.com.superior.adapter;

//public class ExpandTaskAdapter extends MuliteRecycleAdapter {
//
//    private Map<String, String[]> childCategorys = new HashMap<String, String[]>() {
//        {
//            put("2", new String[]{"", "第一名", "第二名", "第三名", "第四名", "第五名", "第六名", "第七名"});
//            put("3", new String[]{"", "人大", "政协"});
//            put("4", new String[]{"", "全体会议", "常务会议", "县长办公室会议", "专题会议"});
//        }
//    };
//
//    private int category;
//
//    public ExpandTaskAdapter(Activity context, int category, JSONArray array) {
//        super(context, array);
//        this.category = category;
//    }
//
//    @Override
//    public void onConfigGroupKeys(List mChildGroupKeys) {
//        mChildGroupKeys.add("unitTasks");
//    }
//
//    @Override
//    public void onSelectedGroupIndex(List mExpandPositions) {
//        mExpandPositions.add(0);
//    }
//
//    @Override
//    public BaseItemViewHolder onCreateMuliteViewHolder(@NonNull ViewGroup parent, int viewType) {
//        switch (viewType) {
//            case 0:
//                return new OneViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_expand_task, parent, false));
//            case 1:
//                return new ThreeViewHolder(getContext().getLayoutInflater().inflate(R.layout.item_mulite_unittask, parent, false));
//        }
//        return null;
//    }
//
//    @Override
//    public void onBindMuliteViewHolder(@NonNull BaseItemViewHolder holder, int position, JSONObject item) {
//        if (holder == null) return;
//        holder.bindView(position);
//    }
//
//    @Override
//    protected boolean onItemClick(int type, int position, JSONObject item) {
//        int childTaskId = item.getIntValue("childTaskId");
//        int unitTaskId = item.getIntValue("unitTaskId");
//        Intent intent = new Intent(getContext(), UnitTaskActivity.class);
//        intent.putExtra(UnitTaskActivity.KEY_CHILDTASK_ID, childTaskId);
//        intent.putExtra(UnitTaskActivity.KEY_UNITTASK_ID, unitTaskId);
//        intent.putExtra(UnitTaskActivity.KEY_UNITASK_TYPE, UnitTaskActivity.TYPE_UNITTASK_ARRAY);
//        getContext().startActivity(intent);
//        return true;
//    }
//
//    class OneViewHolder extends BaseItemViewHolder {
//        ImageView ivIndicator;
//        TextView tvName;
//        TextView tvLabel;
//
//        TextView tvChildContent;
//
//        public OneViewHolder(@NonNull View convertView) {
//            super(convertView);
//
//            ivIndicator = (ImageView) convertView.findViewById(R.id.iv_indicator);
//
//            tvName = (TextView) convertView.findViewById(R.id.tv_name);
//
//            tvLabel = convertView.findViewById(R.id.tv_label);
//
//            tvChildContent = convertView.findViewById(R.id.tv_childcontent);
//        }
//
//        @Override
//        void bindView(int position) {
//            tvLabel.setText("");
//            JSONObject jsonObject = getItem(position);
//
//            if (jsonObject.containsKey("task")) {
//                JSONObject task = jsonObject.getJSONObject("task");
//                int category = task.getIntValue("category");
//                int childCategory = task.getIntValue("childCategory");
//                tvLabel.setText(LabelUtils.makeTaskTitleLabel(category) + LabelUtils.makeChildCategoryLabel(category, childCategory));
//            }
//
//            tvName.setText(jsonObject.getJSONObject("task").getString("title"));
//
//            tvChildContent.setText(LabelUtils.makeChildTaskTitleLabel(category) + jsonObject.getString("content"));
//        }
//    }
//
//
//    class ThreeViewHolder extends BaseItemViewHolder {
//        AvatarImageView lvLogo;
//        TextView tvName;
//        TextView tvContent;
//        TextView tvStatus;
//        ProgressBar pbProgress;
//        TextView tvProgress;
//
//
//        public ThreeViewHolder(@NonNull View convertView) {
//            super(convertView);
//
//            lvLogo = (AvatarImageView) convertView.findViewById(R.id.lv_logo);
//            tvName = (TextView) convertView.findViewById(R.id.tv_name);
//
//            tvContent = (TextView) convertView.findViewById(R.id.tv_content);
//            tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
//
//            pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_progress);
//            tvProgress = (TextView) convertView.findViewById(R.id.tv_progress);
//
//        }
//
//        @Override
//        void bindView(int position) {
//            JSONObject jsonObject = getItem(position);
//
//            Logger.d("content:" + jsonObject.toJSONString());
//
//
//            tvContent.setText(jsonObject.getString("content"));
//
//
//            tvStatus.setText(StatusConfig.STATUS.get(jsonObject.getString("status")));
//
//            Glide.with(getContext()).load(HttpUrl.ATTACHMENT + jsonObject.getJSONObject("unit").getString("logo")).into(lvLogo);
//            tvName.setText(jsonObject.getJSONObject("unit").getString("name"));
//
//            pbProgress.setProgress(jsonObject.getIntValue("progress"));
//
//            tvProgress.setText(jsonObject.getString("progress") + "%");
//        }
//    }
//}

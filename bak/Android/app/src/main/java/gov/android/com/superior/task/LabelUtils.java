package gov.android.com.superior.task;

import com.orhanobut.logger.Logger;

public class LabelUtils {

    public static String makeTaskTitleLabel(int category) {
        if (category < StatusConfig.taskTitleLabels.size()) {
            return StatusConfig.taskTitleLabels.get(category + "") + "：";
        }
        return "";
    }

    public static String makeChildTaskTitleLabel(int category) {
        if (category < StatusConfig.childTaskTitleLabels.size()) {
            return StatusConfig.childTaskTitleLabels.get(category + "")+"：";
        }
        return "";
    }

    public static String makeChildCategoryLabel(int category, int childCategory) {
        if (StatusConfig.childTaskCategoryNames.containsKey(category + "")) {
            String[] childCategoryNames = StatusConfig.childTaskCategoryNames.get(category + "");
            if (childCategory < childCategoryNames.length) {
                return "【" +childCategoryNames[childCategory] + "】";
            }
        }
        return "";
    }

}

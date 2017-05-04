package cn.j1angvei.castk2;


/**
 * start task list in introduction
 * Created by Wayne on 2016/11/23.
 */
public enum Task {
    HELP("-h", "print help information and usage"),
    PIPELINE("-p", "ChIP-Seq analysis pipeline"),
    FUNCTION("-f", "start function(s) in order"),
    STANDALONE("-s", "start standalone function with arguments"),
    INSTALL("-i", "(re)install all software"),
    RESET("-r", "reset project to original state"),
    BACKUP("-b", "backup all file of last analysis"),
    PEAK_TYPE("-t", "print broad,narrow,mix peak type information"),
    SPECIES("-c", "print species code information");
    private String keyword;
    private String description;

    Task(String keyword, String description) {
        this.keyword = keyword;
        this.description = description;
    }

    public static Task fromKeyword(String keyword) {
        for (Task task : Task.values()) {
            if (task.getKeyword().toLowerCase().equals(keyword)) {
                return task;
            }
        }
        throw new IllegalArgumentException("keyword " + keyword + " not found in " + Task.class);
    }

    public static String getTaskUsage() {
        StringBuilder builder = new StringBuilder();
        for (Task task : Task.values()) {
            builder.append(task.toString())
                    .append("\n");
        }
        return builder.toString();
    }

    public String getKeyword() {
        return keyword;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("\t%s,\t%s", this.getKeyword(), this.getDescription());
    }

}

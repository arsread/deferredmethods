package test;

public class TaskUser {

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    public void usingTask() {
        task.doSomething();
        for (int i = 0; i < 10; i++) {
            Math.pow(i, i);
        }
    }
}

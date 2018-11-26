package com.ekold.threadpool;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/12
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TaskExecutePool.class);

        AsyncTask asyncTask = context.getBean(AsyncTask.class);

        for (int i = 0; i < 20; i++) {
//            asyncTask.taskAsync(i);
        }
        context.close();
    }
}
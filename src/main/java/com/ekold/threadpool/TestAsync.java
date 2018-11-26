package com.ekold.threadpool;

import com.ekold.OldorderApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OldorderApplication.class)
public class TestAsync {

    @Autowired
    private AsyncTask aysncTask;

    @Test
    public void testAsync() throws IOException {
        try {
            aysncTask.taskAsync("20180101", 0, 10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
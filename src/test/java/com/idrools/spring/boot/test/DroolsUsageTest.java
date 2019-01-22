package com.idrools.spring.boot.test;

import com.idrools.spring.boot.SpringBootStarterDroolsApplicationTests;
import com.idrools.spring.boot.test.model.Account;
import com.idrools.spring.boot.test.model.CashFlow;
import com.idrools.spring.boot.test.utils.DateHelper;
import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.common.ProjectClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

import static org.drools.compiler.kie.builder.impl.ClasspathKieProject.fixURLFromKProjectPath;

/**
 * 说明
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootStarterDroolsApplicationTests.class)
public class DroolsUsageTest {
    private static final Logger logger = LoggerFactory.getLogger(DroolsUsageTest.class);
    @Autowired
    private KieSession kieSession;

    @Test
    public void testCashFlow() throws Exception{
        Account account = new Account();
        account.setAccountNo(1);
        account.setBalance(0);
        kieSession.insert(account);
        CashFlow cashFlow=new CashFlow();
        cashFlow.setAccountNo(1);
        cashFlow.setType(CashFlow.CREDIT);
        cashFlow.setAmount(1000);
        cashFlow.setMvtDate(DateHelper.getDate("2018-12-08"));
        kieSession.insert(cashFlow);
        int rulefireCount = kieSession.fireAllRules();
        Assert.assertEquals(rulefireCount,1);

    }
    @Test
    public void testClasspath() throws Exception{
        ClassLoader classLoader=  ProjectClassLoader.findParentClassLoader();
        String configFiles = "META-INF/kmodule.xml";
//        URL url = (URL)classLoader.getResource().nextElement();
        URL url = (URL) classLoader.getResources(configFiles).nextElement();
        String pomProperties = ClasspathKieProject.getPomProperties(fixURLFromKProjectPath(url));
        ReleaseId releaseId = pomProperties != null ? ReleaseIdImpl.fromPropertiesString(pomProperties) : KieServices.Factory.get().getRepository().getDefaultReleaseId();


    }
}

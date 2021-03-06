package com.idrools.spring.boot.configuration;

import org.drools.compiler.kie.builder.impl.ClasspathKieProject;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.common.ProjectClassLoader;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import static org.drools.compiler.kie.builder.impl.ClasspathKieProject.fixURLFromKProjectPath;

/**
 * created by idrools007 2019/1/20
 */
@Configuration
public class DroolsAutoConfiguration {
    private static final String RULES_PATH = "rules/";

    private static final String CONFIG_RULE = "META-INF/kmodule.xml";
    private static final String CONFIG_RULE_SPRING = "META-INF/kmodule-spring.xml";
    @Bean
    @ConditionalOnMissingBean(KieFileSystem.class)
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        for (Resource file : getRuleFiles()) {
            kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
        }
        return kieFileSystem;
    }

    private Resource[] getRuleFiles() throws IOException {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        return resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
    }

    @Bean
    @ConditionalOnMissingBean(KieContainer.class)
    public KieContainer kieContainer() throws IOException {
        final KieRepository kieRepository = getKieServices().getRepository();

        kieRepository.addKieModule(new KieModule() {
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });

        KieBuilder kieBuilder = getKieServices().newKieBuilder(kieFileSystem());
        kieBuilder.buildAll();

        return getKieServices().newKieContainer(kieRepository.getDefaultReleaseId());
    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    @ConditionalOnMissingBean(KieBase.class)
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }

    @Bean
    @ConditionalOnMissingBean(KieSession.class)
    public KieSession kieSession() throws IOException {
        return kieContainer().newKieSession();
    }
    @Bean
    @ConditionalOnMissingBean(ReleaseId.class)
    public ReleaseId releaseId() throws IOException {
        ClassLoader classLoader=  ProjectClassLoader.findParentClassLoader();
        Enumeration<URL> enumeration = classLoader.getResources(CONFIG_RULE);
        if (!enumeration.hasMoreElements()) {
            enumeration= classLoader.getResources(CONFIG_RULE_SPRING);
        }
        String pomProperties = ClasspathKieProject.getPomProperties(fixURLFromKProjectPath(enumeration.nextElement()));
        return pomProperties != null ? ReleaseIdImpl.fromPropertiesString(pomProperties) : KieServices.Factory.get().getRepository().getDefaultReleaseId();
    }

    @Bean
    @ConditionalOnMissingBean(KModuleBeanFactoryPostProcessor.class)
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }

}

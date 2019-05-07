package com.example.processorlib;

import com.example.annotationlib.LActivity;
import com.example.annotationlib.LView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class LProcessor extends AbstractProcessor {

    private Elements elementsUtil;


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(LActivity.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(LActivity.class);
        for (Element element : elements) {
            // 判断是否Class
            TypeElement typeElement = (TypeElement) element;
            List<? extends Element> members = elementsUtil.getAllMembers(typeElement);
            MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(ClassName.get(typeElement.asType()), "activity");
            for (Element item : members) {
                LView diView = item.getAnnotation(LView.class);
                if (diView == null) {
                    continue;
                }
                bindViewMethodSpecBuilder.addStatement(String.format("activity.%s = (%s) activity.findViewById(%s)", item.getSimpleName(), ClassName.get(item.asType()).toString(), diView.value()));
            }
            TypeSpec typeSpec = TypeSpec.classBuilder("DI" + element.getSimpleName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(bindViewMethodSpecBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpec).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private String getPackageName(TypeElement typeElement) {
        return elementsUtil.getPackageOf(typeElement).getQualifiedName().toString();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementsUtil = processingEnvironment.getElementUtils();
    }


}

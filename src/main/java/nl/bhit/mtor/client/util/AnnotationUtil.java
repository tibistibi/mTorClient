package nl.bhit.mtor.client.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * will make life easier for working with annotations.
 * 
 * @author tibi
 */
public final class AnnotationUtil {
	private static final transient Logger LOG = Logger.getLogger(AnnotationUtil.class);
	
    private static final ClassPathScanningCandidateComponentProvider PROVIDER = new ClassPathScanningCandidateComponentProvider(false);

    /**
     * will search for annotation of type searchForAnnotation within the basePackage
     * 
     * @param searchForAnnotation
     *            the annotation to search for.
     * @param basePackage
     *            the package to search in (recursive).
     * @return the found candidates.
     */
    public static Set<BeanDefinition> findProviders(Class<? extends Annotation> searchForAnnotation, String basePackage) {
    	PROVIDER.addIncludeFilter(new AnnotationTypeFilter(searchForAnnotation));
    	PROVIDER.setResourceLoader(new PathMatchingResourcePatternResolver(AnnotationUtil.class.getClassLoader()));
        return PROVIDER.findCandidateComponents(basePackage);
    }

    /**
     * will search for the method annotation of type searchForMethodAnnotation within the beanDefinition
     * 
     * @param searchForMethodAnnotation
     *            the method annotation to search for.
     * @param beanDefinition
     *            the bean to search in.
     * @return the found methods.
     */
    public static List<Method> findMethods(Class<? extends Annotation> searchForMethodAnnotation,
            BeanDefinition beanDefinition) {
        List<Method> methods = new ArrayList<Method>();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(beanDefinition.getBeanClassName());
        } catch (ClassNotFoundException e) {
            LOG.warn("Class not found, return null.");
            LOG.debug("Error belonging to: class not found, return null.", e);
            return null;
        }
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(searchForMethodAnnotation)) {
                methods.add(method);
            }
        }
        return methods;
    }
    
	private AnnotationUtil() {
	}
}

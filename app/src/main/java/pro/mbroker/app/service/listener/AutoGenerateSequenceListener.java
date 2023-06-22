package pro.mbroker.app.service.listener;

import pro.mbroker.app.annotations.AutoGenerateSequence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;
import javax.persistence.Query;
import java.util.Arrays;

public class AutoGenerateSequenceListener {

    @PersistenceContext
    private EntityManager em;

    @PrePersist
    public void prePersist(Object object) {
        Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.getAnnotation(AutoGenerateSequence.class) != null)
                .findFirst()
                .ifPresent(field -> {
                    field.setAccessible(true);
                    try {
                        if (field.get(object) == null) {
                            Query query = em.createNativeQuery("SELECT nextval('application_number_seq')");
                            Integer result = (Integer) query.getSingleResult();
                            field.set(object, result);
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}

package akc.plugin.playerpenalty.domain.configuration;

import akc.plugin.playerpenalty.domain.entities.TicketEntity;

import java.util.List;

public class EntityScanner {

    public List<Class<?>> getEntityClasses() {
        TicketEntity.class.getPermittedSubclasses();
        return List.of(TicketEntity.class);
    }
}

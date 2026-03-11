/**
 * LogService.java
 *
 * Created on 3. 10. 2021, 20:38:33 by burgetr
 */
package io.github.radkovo.jwtlogin.dao;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import io.github.radkovo.jwtlogin.data.LogEntry;

/**
 * 
 * @author burgetr
 */
@ApplicationScoped
@Transactional
public class LogService
{
    @PersistenceContext(unitName = "usersPU")
    EntityManager em;

    public void log(LogEntry entry)
    {
        em.merge(entry);
    }
    
    public List<LogEntry> getEntries()
    {
        return em.createNamedQuery("LogEntry.all", LogEntry.class).getResultList();
    }
    
}

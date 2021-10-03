/**
 * LogService.java
 *
 * Created on 3. 10. 2021, 20:38:33 by burgetr
 */
package io.github.radkovo.jwtlogin.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import io.github.radkovo.jwtlogin.data.LogEntry;

/**
 * 
 * @author burgetr
 */
@Stateless
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

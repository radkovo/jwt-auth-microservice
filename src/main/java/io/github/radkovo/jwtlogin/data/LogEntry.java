/**
 * LogEntry.java
 *
 * Created on 3. 10. 2021, 20:33:36 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Table;

/**
 * 
 * @author burgetr
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "LogEntry.all", query = "select e from LogEntry e order by e.timestamp")
})
@Table(name = "log")
public class LogEntry
{
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;
    @Temporal(TIMESTAMP)
    private Date timestamp;
    private String realm;
    private String action;
    private String subject;
    private String description;

    public LogEntry()
    {
    }
    
    public LogEntry(String realm, String action, String subject,
            String description)
    {
        this.timestamp = new Date();
        this.realm = realm;
        this.action = action;
        this.subject = subject;
        this.description = description;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getRealm()
    {
        return realm;
    }

    public void setRealm(String realm)
    {
        this.realm = realm;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}

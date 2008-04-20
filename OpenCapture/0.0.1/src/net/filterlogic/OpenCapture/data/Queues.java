/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author D106931
 */
@Entity
@Table(name = "queues")
@NamedQueries({@NamedQuery(name = "Queues.findByQueueId", query = "SELECT q FROM Queues q WHERE q.queueId = :queueId"), @NamedQuery(name = "Queues.findByQueueName", query = "SELECT q FROM Queues q WHERE q.queueName = :queueName"), @NamedQuery(name = "Queues.findByQueueDesc", query = "SELECT q FROM Queues q WHERE q.queueDesc = :queueDesc"), @NamedQuery(name = "Queues.findByPlugin", query = "SELECT q FROM Queues q WHERE q.plugin = :plugin")})
public class Queues implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUEUE_ID", nullable = false)
    private Long queueId;
    @Column(name = "QUEUE_NAME", nullable = false)
    private String queueName;
    @Column(name = "QUEUE_DESC")
    private String queueDesc;
    @Column(name = "PLUGIN")
    private String plugin;

    public Queues() {
    }

    public Queues(Long queueId) {
        this.queueId = queueId;
    }

    public Queues(Long queueId, String queueName) {
        this.queueId = queueId;
        this.queueName = queueName;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueDesc() {
        return queueDesc;
    }

    public void setQueueDesc(String queueDesc) {
        this.queueDesc = queueDesc;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (queueId != null ? queueId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Queues)) {
            return false;
        }
        Queues other = (Queues) object;
        if ((this.queueId == null && other.queueId != null) || (this.queueId != null && !this.queueId.equals(other.queueId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.filterlogic.OpenCapture.data.Queues[queueId=" + queueId + "]";
    }

}

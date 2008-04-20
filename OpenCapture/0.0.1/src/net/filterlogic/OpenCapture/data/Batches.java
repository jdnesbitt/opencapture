/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author dnesbitt
 */
@Entity
@Table(name = "batches")
@NamedQueries({@NamedQuery(name = "Batches.findByBatchId", query = "SELECT b FROM Batches b WHERE b.batchId = :batchId"), @NamedQuery(name = "Batches.findByBatchName", query = "SELECT b FROM Batches b WHERE b.batchName = :batchName"), @NamedQuery(name = "Batches.findByBatchClassId", query = "SELECT b FROM Batches b WHERE b.batchClassId = :batchClassId"), @NamedQuery(name = "Batches.findByScanDatetime", query = "SELECT b FROM Batches b WHERE b.scanDatetime = :scanDatetime"), @NamedQuery(name = "Batches.findBySiteId", query = "SELECT b FROM Batches b WHERE b.siteId = :siteId"), @NamedQuery(name = "Batches.findByBatchState", query = "SELECT b FROM Batches b WHERE b.batchState = :batchState"), @NamedQuery(name = "Batches.findByErrorNo", query = "SELECT b FROM Batches b WHERE b.errorNo = :errorNo"), @NamedQuery(name = "Batches.findByBatchDesc", query = "SELECT b FROM Batches b WHERE b.batchDesc = :batchDesc"), @NamedQuery(name = "Batches.findByPriority", query = "SELECT b FROM Batches b WHERE b.priority = :priority"), @NamedQuery(name = "Batches.findByQueueId", query = "SELECT b FROM Batches b WHERE b.queueId = :queueId"), @NamedQuery(name = "Batches.getNextBatchByQueueId", query = "SELECT b FROM Batches where b.queue_id = :queueId and batch_state = 0 order by scan_datetime,priority desc limit 1")})
public class Batches implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BATCH_ID", nullable = false)
    private Long batchId;
    @Column(name = "BATCH_NAME", nullable = false)
    private String batchName;
    @Column(name = "BATCH_CLASS_ID", nullable = false)
    private long batchClassId;
    @Column(name = "SCAN_DATETIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date scanDatetime;
    @Column(name = "SITE_ID", nullable = false)
    private int siteId;
    @Column(name = "BATCH_STATE", nullable = false)
    private int batchState;
    @Column(name = "ERROR_NO")
    private Integer errorNo;
    @Lob
    @Column(name = "ERROR_MSG")
    private String errorMsg;
    @Column(name = "BATCH_DESC")
    private String batchDesc;
    @Column(name = "PRIORITY")
    private Short priority;
    @Column(name = "QUEUE_ID", nullable = false)
    private long queueId;

    public Batches()
    {
    }

    public Batches(Long batchId)
    {
        this.batchId = batchId;
    }

    public Batches(Long batchId, String batchName, long batchClassId, Date scanDatetime, int siteId, int batchState, long queueId)
    {
        //this.batchId = batchId;
        this.batchName = batchName;
        this.batchClassId = batchClassId;
        this.scanDatetime = scanDatetime;
        this.siteId = siteId;
        this.batchState = batchState;
        this.queueId = queueId;
    }

    public Long getBatchId()
    {
        return batchId;
    }

    public void setBatchId(Long batchId)
    {
        this.batchId = batchId;
    }

    public String getBatchName()
    {
        return batchName;
    }

    public void setBatchName(String batchName)
    {
        this.batchName = batchName;
    }

    public long getBatchClassId()
    {
        return batchClassId;
    }

    public void setBatchClassId(long batchClassId)
    {
        this.batchClassId = batchClassId;
    }

    public Date getScanDatetime()
    {
        return scanDatetime;
    }

    public void setScanDatetime(Date scanDatetime)
    {
        this.scanDatetime = scanDatetime;
    }

    public int getSiteId()
    {
        return siteId;
    }

    public void setSiteId(int siteId)
    {
        this.siteId = siteId;
    }

    public int getBatchState()
    {
        return batchState;
    }

    public void setBatchState(int batchState)
    {
        this.batchState = batchState;
    }

    public Integer getErrorNo()
    {
        return errorNo;
    }

    public void setErrorNo(Integer errorNo)
    {
        this.errorNo = errorNo;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public String getBatchDesc()
    {
        return batchDesc;
    }

    public void setBatchDesc(String batchDesc)
    {
        this.batchDesc = batchDesc;
    }

    public Short getPriority()
    {
        return priority;
    }

    public void setPriority(Short priority)
    {
        this.priority = priority;
    }

    public long getQueueId()
    {
        return queueId;
    }

    public void setQueueId(long queueId)
    {
        this.queueId = queueId;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (batchId != null ? batchId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Batches))
        {
            return false;
        }
        Batches other = (Batches) object;
        if ((this.batchId == null && other.batchId != null) || (this.batchId != null && !this.batchId.equals(other.batchId)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "net.filterlogic.OpenCapture.data.Batches[batchId=" + batchId + "]";
    }

}

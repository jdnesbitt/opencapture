/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
@Embeddable
public class LkpBatchclassQueuesPK implements Serializable {
    @Column(name = "QUEUE_ID", nullable = false)
    private long queueId;
    @Column(name = "BATCH_CLASS_ID", nullable = false)
    private long batchClassId;

    public LkpBatchclassQueuesPK() {
    }

    public LkpBatchclassQueuesPK(long queueId, long batchClassId) {
        this.queueId = queueId;
        this.batchClassId = batchClassId;
    }

    public long getQueueId() {
        return queueId;
    }

    public void setQueueId(long queueId) {
        this.queueId = queueId;
    }

    public long getBatchClassId() {
        return batchClassId;
    }

    public void setBatchClassId(long batchClassId) {
        this.batchClassId = batchClassId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) queueId;
        hash += (int) batchClassId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LkpBatchclassQueuesPK)) {
            return false;
        }
        LkpBatchclassQueuesPK other = (LkpBatchclassQueuesPK) object;
        if (this.queueId != other.queueId) {
            return false;
        }
        if (this.batchClassId != other.batchClassId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.filterlogic.OpenCapture.data.LkpBatchclassQueuesPK[queueId=" + queueId + ", batchClassId=" + batchClassId + "]";
    }

}

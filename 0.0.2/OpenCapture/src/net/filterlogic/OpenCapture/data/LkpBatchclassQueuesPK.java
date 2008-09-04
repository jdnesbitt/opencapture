/*
Copyright 2008 Filter Logic

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
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

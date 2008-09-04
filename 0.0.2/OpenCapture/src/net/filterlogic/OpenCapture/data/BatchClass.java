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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
@Entity
@Table(name = "batch_class")
@NamedQueries({@NamedQuery(name = "BatchClass.findByBatchClassId", query = "SELECT b FROM BatchClass b WHERE b.batchClassId = :batchClassId"), @NamedQuery(name = "BatchClass.findByBatchClassName", query = "SELECT b FROM BatchClass b WHERE b.batchClassName = :batchClassName"), @NamedQuery(name = "BatchClass.findByDescr", query = "SELECT b FROM BatchClass b WHERE b.descr = :descr"), @NamedQuery(name = "BatchClass.findByImagePath", query = "SELECT b FROM BatchClass b WHERE b.imagePath = :imagePath")})
public class BatchClass implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BATCH_CLASS_ID", nullable = false)
    private Long batchClassId;
    @Column(name = "BATCH_CLASS_NAME", nullable = false)
    private String batchClassName;
    @Column(name = "DESCR")
    private String descr;
    @Column(name = "IMAGE_PATH", nullable = false)
    private String imagePath;

    public BatchClass() {
    }

    public BatchClass(Long batchClassId) {
        this.batchClassId = batchClassId;
    }

    public BatchClass(Long batchClassId, String batchClassName, String batchClassDescr, String imagePath) {
        this.batchClassId = batchClassId;
        this.batchClassName = batchClassName;
        this.descr = batchClassDescr;
        this.imagePath = imagePath;
    }

    public Long getBatchClassId() {
        return batchClassId;
    }

    public void setBatchClassId(Long batchClassId) {
        this.batchClassId = batchClassId;
    }

    public String getBatchClassName() {
        return batchClassName;
    }

    public void setBatchClassName(String batchClassName) {
        this.batchClassName = batchClassName;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (batchClassId != null ? batchClassId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BatchClass)) {
            return false;
        }
        BatchClass other = (BatchClass) object;
        if ((this.batchClassId == null && other.batchClassId != null) || (this.batchClassId != null && !this.batchClassId.equals(other.batchClassId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "net.filterlogic.OpenCapture.data.BatchClass[batchClassId=" + batchClassId + "]";
    }

}

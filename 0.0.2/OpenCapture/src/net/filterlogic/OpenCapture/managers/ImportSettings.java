/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.managers;

/**
 *
 * @author dnesbitt
 */
    public class ImportSettings
    {
        private String imagePath = "";
        private String archivePath = "";
        private String batchClass = "";

        private String pollDir = "";
        private boolean filesOnly = false;
        private boolean processSubFolder = false;
        private boolean documentPerFile = false;
        private boolean batchPerFile = false;
        private String triggerExt = "";
        private String mappingFile = "";
        private boolean useMappingFile = false;
        private MappingFile mappingFileObj = null;

        private String batchNamePrefix = "";
        private String batchNameSuffix = "";

        private String importPlugin = "";

        public ImportSettings()
        {
        }

        public String getBatchNamePrefix() {
            return batchNamePrefix;
        }

        public void setBatchNamePrefix(String batchNamePrefix) {
            this.batchNamePrefix = batchNamePrefix;
        }

        public String getBatchNameSuffix() {
            return batchNameSuffix;
        }

        public void setBatchNameSuffix(String batchNameSuffix) {
            this.batchNameSuffix = batchNameSuffix;
        }

        public boolean isUseMappingFile() {
            return useMappingFile;
        }

        public void setUseMappingFile(boolean useMappingFile) {
            this.useMappingFile = useMappingFile;
        }


        public String getArchivePath() {
            return archivePath;
        }

        public void setArchivePath(String archivePath) {
            this.archivePath = archivePath;
        }

        public String getBatchClass() {
            return batchClass;
        }

        public void setBatchClass(String batchClass) {
            this.batchClass = batchClass;
        }

        public boolean isBatchPerFile() {
            return batchPerFile;
        }

        public void setBatchPerFile(boolean batchPerFile) {
            this.batchPerFile = batchPerFile;
        }

        public boolean isDocumentPerFile() {
            return documentPerFile;
        }

        public void setDocumentPerFile(boolean documentPerFile) {
            this.documentPerFile = documentPerFile;
        }

        public boolean isFilesOnly() {
            return filesOnly;
        }

        public void setFilesOnly(boolean filesOnly) {
            this.filesOnly = filesOnly;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getMappingFile() {
            return mappingFile;
        }

        public void setMappingFile(String mappingFile) {
            this.mappingFile = mappingFile;
        }

        public String getPollDir() {
            return pollDir;
        }

        public void setPollDir(String pollDir) {
            this.pollDir = pollDir;
        }

        public boolean isProcessSubFolder() {
            return processSubFolder;
        }

        public void setProcessSubFolder(boolean processSubFolder) {
            this.processSubFolder = processSubFolder;
        }

        public String getTriggerExt() {
            return triggerExt;
        }

        public void setTriggerExt(String triggerExt) {
            this.triggerExt = triggerExt;
        }

        public MappingFile getMappingFileObj() {
            return mappingFileObj;
        }

        public void setMappingFileObj(MappingFile mappingFileObj) {
            this.mappingFileObj = mappingFileObj;
        }

        public String getImportPlugin() {
            return importPlugin;
        }

        public void setImportPlugin(String importPlugin) {
            this.importPlugin = importPlugin;
        }


    }

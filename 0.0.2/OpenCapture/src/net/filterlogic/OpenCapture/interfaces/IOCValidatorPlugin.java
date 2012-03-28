/*
 * This interface is used to create custom validators that
 * process and object value passed to the ExecuteValidator method.
 * 
 */

package net.filterlogic.OpenCapture.interfaces;

/**
 *
 * @author dnesbitt
 */
public interface IOCValidatorPlugin
{
    /**
     * Get name of OC Validator plugin.
     *
     * @return String name of plugin.
     */
    public String getName();

    /**
     * Get the OC Validator plugin description.  The description should contain
     * information about the purpose of the plugin.
     *
     * @return String description of plugin.
     */
    public String getDescription();

    /**
     * Execute the validator.
     *
     * @param validatorType Identifies the type of validator and when it's executed.<br/><br/>
     *
     * PRE - Run at the time the batch/index field rendered.<br/>
     * AUTO - Run when as value is typed in batch/index field.<br/>
     * POST - Rung when batch/index field looses focus.<br/>
     *
     * @param validatorKey Input field name.
     * @param validatorValue Value to be validated.
     *
     * @return Object result.
     */
    public Object ExecuteValidator(String validatorType, Object validatorKey, Object validatorValue);
}

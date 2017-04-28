package com.codeaffine.home.control.engine.preference;

class Messages {
  static final String ERROR_NOT_A_PREFERENCE = "Expect <%s> to be annotated with @Preference.";
  static final String ERROR_NOT_A_INTERFACE = "Expect <%s> to be an interface.";
  static final String ERROR_INVALID_BEAN_PROPERTY_ACCESSOR = "Method <%s> of preference type <%s> is not a valid bean property accessor.";
  static final String ERROR_UNDEFINED_PREFERENCE_VALUE = "Preference value of attribute <%s> has not been defined.";
  static final String ERROR_UNSUPPORTED_DEFAULT_VALUE_TYPE = "Value type of attribute <%s> does not provide static 'valueOf(String)' factory method for default value annotation support.";
  static final String ERROR_MISSING_READ_ACCESSOR = "Preference <%s> has no read accessor for attribute <%s>.";
  static final String ERROR_MISSING_WRITE_ACCESSOR = "Preference <%s> has no write accessor for attribute <%s>.";
  static final String ERROR_MISSING_DEFAULT_VALUE_DEFINITION = "Preference <%s> has no default value annotation for attribute <%s>.";
  static final String ERROR_UNSUPPORTED_ATTRIBUTE_TYPE = "Preference <%s> has attribute <%s> that uses an unsupported type or an type with unsupported generic parameters <%s>.";
  static final String ERROR_SAVING_MODEL = "Unable to write preferences to output stream.";
  static final String ERROR_CONFIGURATION_DIR_NOT_SET = "'%s' environment variable is not set.";
}
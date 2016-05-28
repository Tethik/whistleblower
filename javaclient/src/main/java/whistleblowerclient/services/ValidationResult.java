package whistleblowerclient.services;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tethik on 06/01/16.
 */
public class ValidationResult {

    public boolean pass() {
      return errors.size() == 0;
    }

    public List<String> errors = new ArrayList<>();

    public synchronized void addError(String error) {
        errors.add(error);
    }

    public String toString() {
        if(pass()) {
            return "Validation passed.";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("Validation failed:\n");
        errors.stream().forEach(s -> builder.append(s).append("\n"));
        return builder.toString();
    }
}

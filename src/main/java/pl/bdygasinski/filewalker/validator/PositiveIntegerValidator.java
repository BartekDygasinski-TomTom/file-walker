package pl.bdygasinski.filewalker.validator;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class PositiveIntegerValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
        try {
            int n = Integer.parseInt(value);
            if (n <= 0) {
                throw new ParameterException(name + " must be a positive integer. Given: " + value);
            }
        } catch (NumberFormatException e) {
            throw new ParameterException(name + " must be a valid integer. Given: " + value);
        }
    }
}

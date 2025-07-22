package gift.option;

import gift.option.entity.OptionName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OptionNameConverter implements AttributeConverter<OptionName, String> {

    @Override
    public String convertToDatabaseColumn(OptionName attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public OptionName convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new OptionName(dbData);
    }
}
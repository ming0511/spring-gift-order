package gift.option.entity;

public class OptionName {

    private String value;

    public OptionName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null) {
            throw new IllegalArgumentException("옵션 이름은 비어있을 수 없습니다.");
        }
        if (value.length() > 50) {
            throw new IllegalArgumentException("옵션 이름은 50자를 초과할 수 없습니다.");
        }
        if (!value.matches("[\\p{L}\\p{N} ()\\[\\]+\\-&/_]*")) {
            throw new IllegalArgumentException("옵션 이름에 허용되지 않은 문자가 포함되어 있습니다.");
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptionName)) {
            return false;
        }
        OptionName that = (OptionName) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}

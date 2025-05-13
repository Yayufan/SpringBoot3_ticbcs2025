package tw.com.ticbcs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckinActionTypeEnum {
	CHECKIN(1, "簽到"), CHECKOUT(2, "簽退");

	private final Integer value;
	private final String label;

	public static CheckinActionTypeEnum fromValue(Integer value) {
		for (CheckinActionTypeEnum type : values()) {
			if (type.value == value)
				return type;
		}
		throw new IllegalArgumentException("無效的簽到行為值: " + value);
	}
}

package com.akatsuki.newsum.common.enums;

import java.lang.reflect.ParameterizedType;

import com.fasterxml.jackson.annotation.JsonValue;

public abstract class EnumString<E extends Enum<E>> {
	private final String value;

	//자식클래스에서만 호출할 수 있도록 protected
	protected EnumString(String value) {
		this.value = value;
	}

	//객체 -> json 변환시 사용
	@JsonValue
	public String getValue() {
		return value;
	}

	//string -> enum 변환시 실패하면 예외
	public E toEnumOrThrow() {
		try {
			return Enum.valueOf(getEnumClass(), value);
		} catch (Exception e) {
			throw new IllegalArgumentException(
				"\"" + value + "\"는 유효한 값이 아닙니다. Enum 버전을 확인해주세요."
			);
		}
	}

	//string -> enum 변환시 실패하면 기본값
	public E toEnumOrElse(E defaultValue) {
		try {
			return Enum.valueOf(getEnumClass(), value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	//string -> enum 변환시 실패하면 null
	public E toEnumOrNull() {
		try {
			return Enum.valueOf(getEnumClass(), value);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked") //경고를 안띄우도록
	private Class<E> getEnumClass() {
		//상속된 클래스 확인
		ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
		return (Class<E>)type.getActualTypeArguments()[0];

	}
}

package com.maven.neuto.payload.request.language;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Locale;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocalHolder {
    private Locale currentLocale;
}

package com.mousefeed.eclipse.preferences.presentation;

import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public class SchemeConfig {

    private final String schemeId;

    private RGB colorCode = new RGB(0, 0, 0);

    private String label = "";

    public SchemeConfig(String schemeId, RGB colorCode, String label) {
        super();
        this.schemeId = schemeId;
        this.colorCode = colorCode;
        this.label = label;
    }

    public SchemeConfig(String schemeId) {
        this.schemeId = schemeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((schemeId == null) ? 0 : schemeId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SchemeConfig other = (SchemeConfig) obj;
        if (schemeId == null) {
            if (other.schemeId != null)
                return false;
        } else if (!schemeId.equals(other.schemeId))
            return false;
        return true;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public RGB getColorCode() {
        return colorCode;
    }

    public void setColorCode(RGB colorCode) {
        this.colorCode = colorCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}

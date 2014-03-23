package com.mousefeed.eclipse.preferences;

import com.mousefeed.eclipse.Activator;
import com.mousefeed.eclipse.preferences.presentation.SchemeConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

/**
 * 
 * @author Rabea Gransberger (@rgransberger)
 */
public class PresentationModePreferences {

    private static final String PREF_FILE = "presentationMode.xml";

    private static final String TAG_ROOT = "schemeConfig";

    private static final String TAG_RGB = "rgb";

    private static final String TAG_ID = "id";

    /**
     * The action label tag name.
     */
    private static final String TAG_LABEL = "label";

    private static final String PREF_PARENTHESES = "parentheses";

    private final List<SchemeConfig> list = new ArrayList<SchemeConfig>();

    private boolean useParentheses = false;

    // ###

    public Collection<SchemeConfig> values() {
        return list;
    }

    public void setUseParentheses(boolean useParentheses) {
        this.useParentheses = useParentheses;
    }

    public boolean isUseParentheses() {
        return useParentheses;
    }

    public void add(SchemeConfig config) {
        list.add(config);
    }

    public void remove(String schemeId) {
        for (Iterator<SchemeConfig> it = list.iterator(); it.hasNext();) {
            SchemeConfig next = it.next();
            if (next.getSchemeId().equals(schemeId)) {
                it.remove();
                break;
            }
        }

    }

    public void defaults() {
        this.useParentheses = false;

        list.clear();

        list.add(new SchemeConfig("org.eclipse.ui.defaultAcceleratorConfiguration", new RGB(0, 0, 0), ""));
        // list.add(new SchemeConfig("org.netbeans.scheme.default", new RGB(0,
        // 0, 255), "N"));
        // list.add(new SchemeConfig("com.intellij.scheme.default", new RGB(0,
        // 255, 0), "JI"));
    }

    // ##

    public void load() {
        list.clear();

        final File file = getFile();
        if (!file.exists() || file.length() == 0) {
            // the file not initialized yet
            defaults();
            return;
        }
        Reader reader = null;
        try {
            reader = new FileReader(file);
            final XMLMemento memento = XMLMemento.createReadRoot(reader);
            this.useParentheses = memento.getBoolean(PREF_PARENTHESES) == Boolean.TRUE;
            this.list.addAll(loadFromMemento(memento));
        } catch (final FileNotFoundException ignore) {
            // the file does not exist yet
        } catch (final WorkbenchException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void save() {
        final XMLMemento memento = createMemento();
        Writer writer = null;
        try {
            writer = new FileWriter(getFile());
            memento.save(writer);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ######

    private List<SchemeConfig> loadFromMemento(final XMLMemento memento) {
        final List<SchemeConfig> list = new ArrayList<SchemeConfig>();
        final IMemento[] children = memento.getChildren(TAG_ROOT);
        for (IMemento child : children) {
            final SchemeConfig mode = new SchemeConfig(child.getString(TAG_ID));
            mode.setLabel(child.getString(TAG_LABEL));
            mode.setColorCode(StringConverter.asRGB(child.getString(TAG_RGB)));
            list.add(mode);
        }
        return list;
    }

    private XMLMemento createMemento() {
        final XMLMemento memento = XMLMemento.createWriteRoot("root");
        memento.putBoolean(PREF_PARENTHESES, useParentheses);
        for (SchemeConfig val : list) {
            final IMemento actionMemento = memento.createChild(TAG_ROOT);
            actionMemento.putString(TAG_ID, val.getSchemeId());
            actionMemento.putString(TAG_LABEL, val.getLabel());
            actionMemento.putString(TAG_RGB, StringConverter.asString(val.getColorCode()));
        }
        return memento;
    }

    private File getFile() {
        if (Activator.getDefault() == null) {
            return new File("nonexisting");
        }
        return Activator.getDefault().getStateLocation().append(PREF_FILE).toFile();
    }


}

package org.seasar.dbflute.cbean.coption.parts;


import org.seasar.dbflute.helper.character.GeneralCharacter;
import org.seasar.dbflute.helper.character.impl.GeneralCharacterImpl;

/**
 * The interface of condition-option.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class ToSingleByteOptionParts {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected boolean _toSingleByteSpace;
    protected boolean _toSingleByteAlphabetNumber;
    protected boolean _toSingleByteAlphabetNumberMark;

    private GeneralCharacter _generalCharacter;

    // =====================================================================================
    //                                                                                  Main
    //                                                                                  ====
    public boolean isToSingleByteSpace() {
        return _toSingleByteSpace;
    }
    public void toSingleByteSpace() {
        _toSingleByteSpace = true;
    }

    public void toSingleByteAlphabetNumber() {
        _toSingleByteAlphabetNumber = true;
    }

    public void toSingleByteAlphabetNumberMark() {
        _toSingleByteAlphabetNumberMark = true;
    }

    // =====================================================================================
    //                                                                            Real Value
    //                                                                            ==========
    public String generateRealValue(String value) {
        if (value == null) {
            return value;
        }

        // To Single Byte
        if (_toSingleByteSpace) {
            value = (value != null ? value.replaceAll("\u3000", " ") : value);
        }
        if (_toSingleByteAlphabetNumberMark) {
            value = getGeneralCharacter().toSingleByteAlphabetNumberMark(value);
        } else if (_toSingleByteAlphabetNumber) {
            value = getGeneralCharacter().toSingleByteAlphabetNumber(value);
        }
        return value;
    }

    // =====================================================================================
    //                                                                                Helper
    //                                                                                ======
    protected GeneralCharacter getGeneralCharacter() {
        if (_generalCharacter == null) {
            _generalCharacter = new GeneralCharacterImpl();
        }
        return _generalCharacter;
    }

    // =====================================================================================
    //                                                                              DeepCopy
    //                                                                              ========
    public Object createDeepCopy() {
        final ToSingleByteOptionParts deepCopy = new ToSingleByteOptionParts();
        deepCopy._toSingleByteSpace = _toSingleByteSpace;
        deepCopy._toSingleByteAlphabetNumber = _toSingleByteAlphabetNumber;
        deepCopy._toSingleByteAlphabetNumberMark = _toSingleByteAlphabetNumberMark;
        return deepCopy;
    }
}

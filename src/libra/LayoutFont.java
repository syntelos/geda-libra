package libra;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * An experimental approach to determining string width for labels and
 * other text -- designed for extensions to accomodate platform
 * variance, etc..
 */
public abstract class LayoutFont {

    private static LayoutFont Instance = new LayoutFont.Table();

    public final static LayoutFont Instance(){
	return Instance;
    }

    public final static class Soft
	extends LayoutFont
    {
	private final Font font;

	private final FontMetrics fm;

	private final int height, ascent, leading, em;


	public Soft(){
	    super();
	    this.font = new Font(Font.SANS_SERIF,Font.PLAIN,145);
	    final Toolkit tk = Toolkit.getDefaultToolkit();

	    this.fm = tk.getFontMetrics(this.font);
	    this.height = this.fm.getHeight();
	    this.leading = this.fm.getLeading()+30;
	    this.ascent = this.fm.getAscent();
	    this.em = this.width("m");
	}


	public int em(){
	    return this.em;
	}
	public int height(){
	    return (this.ascent-this.leading);
	}
	public int width(String string){

	    string = LayoutFont.Normalize(string);

	    final int count = string.length();
	    float sw = this.fm.stringWidth(string);
	    float m = ((0.18f * (float)this.em) * (float)count);
	    return (int)(m+sw);
	}
	public int width(char ch){
	    return this.width(String.valueOf(ch));
	}

    }
    public final static class Table 
	extends LayoutFont
    {
        private final static int Height = 170;
        private final static int Ascent = 135;
        private final static int Leading = 30;
        private final static int Em = 141;
        private final static int[] Widths = {
              0, // 0x000 
              0, // 0x001 
              0, // 0x002 
              0, // 0x003 
              0, // 0x004 
              0, // 0x005 
              0, // 0x006 
              0, // 0x007 
              0, // 0x008 
              0, // 0x009 
              0, // 0x00a 
              0, // 0x00b 
              0, // 0x00c 
              0, // 0x00d 
              0, // 0x00e 
              0, // 0x00f 
              0, // 0x010 
              0, // 0x011 
              0, // 0x012 
              0, // 0x013 
              0, // 0x014 
              0, // 0x015 
              0, // 0x016 
              0, // 0x017 
              0, // 0x018 
              0, // 0x019 
              0, // 0x01a 
              0, // 0x01b 
              0, // 0x01c 
              0, // 0x01d 
              0, // 0x01e 
              0, // 0x01f 
             71, // 0x020 
             83, // 0x021 
             92, // 0x022 
            147, // 0x023 
            117, // 0x024 
            163, // 0x025 
            138, // 0x026 
             65, // 0x027 
             82, // 0x028 
             82, // 0x029 
             98, // 0x02a 
            147, // 0x02b 
             71, // 0x02c 
             77, // 0x02d 
             71, // 0x02e 
             50, // 0x02f '/' (edit)
            117, // 0x030 '0' 
            117, // 0x031 '1' 
            117, // 0x032 '2' 
            117, // 0x033 '3' 
            117, // 0x034 '4' 
            117, // 0x035 '5' 
            117, // 0x036 '6' 
            117, // 0x037 '7' 
            117, // 0x038 '8' 
            117, // 0x039 '9' 
             74, // 0x03a 
             74, // 0x03b 
            147, // 0x03c 
            147, // 0x03d 
            147, // 0x03e 
            102, // 0x03f 
            170, // 0x040 
            124, // 0x041 'A' 
            124, // 0x042 'B' 
            131, // 0x043 'C' (edit)
            137, // 0x044 'D' 
            122, // 0x045 'E' (edit)
            108, // 0x046 'F' 
            137, // 0x047 'G' 
            134, // 0x048 'H' 
             68, // 0x049 'I' 
             68, // 0x04a 'J' 
            120, // 0x04b 'K' 
            106, // 0x04c 'L' 
            150, // 0x04d 'M' 
            133, // 0x04e 'N' 
            139, // 0x04f 'O' 
            112, // 0x050 'P' 
            139, // 0x051 'Q' 
            126, // 0x052 'R' 
            122, // 0x053 'S' (edit)
            114, // 0x054 'T' 
            131, // 0x055 'U' 
            124, // 0x056 'V' 
            168, // 0x057 'W' 
            124, // 0x058 'X' 
            114, // 0x059 'Y' 
            124, // 0x05a 'Z' 
             82, // 0x05b 
             74, // 0x05c 
             82, // 0x05d 
              0, // 0x05e 
             98, // 0x05f 
              0, // 0x060 
            114, // 0x061 'a' 
            117, // 0x062 'b' 
            105, // 0x063 'c' 
            117, // 0x064 'd' 
            114, // 0x065 'e' 
             76, // 0x066 'f' 
            117, // 0x067 'g' 
            117, // 0x068 'h' 
             65, // 0x069 'i' 
             65, // 0x06a 'j' 
            109, // 0x06b 'k' 
             65, // 0x06c 'l' 
            166, // 0x06d 'm' 
            117, // 0x06e 'n' 
            114, // 0x06f 'o' 
            117, // 0x070 'p' 
            117, // 0x071 'q' 
             85, // 0x072 'r' 
            101, // 0x073 's' 
             82, // 0x074 't' 
            117, // 0x075 'u' 
            111, // 0x076 'v' 
            144, // 0x077 'w' 
            111, // 0x078 'x' 
            111, // 0x079 'y' 
            101, // 0x07a 'z' 
            117, // 0x07b 
             74, // 0x07c 
            117, // 0x07d 
            147, // 0x07e 
              0, // 0x07f 
              0, // 0x080 
              0, // 0x081 
              0, // 0x082 
              0, // 0x083 
              0, // 0x084 
              0, // 0x085 
              0, // 0x086 
              0, // 0x087 
              0, // 0x088 
              0, // 0x089 
              0, // 0x08a 
              0, // 0x08b 
              0, // 0x08c 
              0, // 0x08d 
              0, // 0x08e 
              0, // 0x08f 
              0, // 0x090 
              0, // 0x091 
              0, // 0x092 
              0, // 0x093 
              0, // 0x094 
              0, // 0x095 
              0, // 0x096 
              0, // 0x097 
              0, // 0x098 
              0, // 0x099 
              0, // 0x09a 
              0, // 0x09b 
              0, // 0x09c 
              0, // 0x09d 
              0, // 0x09e 
              0, // 0x09f 
             71, // 0x0a0 
             83, // 0x0a1 
            117, // 0x0a2 
            117, // 0x0a3 
            117, // 0x0a4 
            117, // 0x0a5 
              0, // 0x0a6 
              0, // 0x0a7 
              0, // 0x0a8 
              0, // 0x0a9 
             93, // 0x0aa 'ª' 
            114, // 0x0ab 
            147, // 0x0ac 
              0, // 0x0ad 
              0, // 0x0ae 
              0, // 0x0af 
              0, // 0x0b0 
            147, // 0x0b1 
             83, // 0x0b2 
             83, // 0x0b3 
              0, // 0x0b4 
            117, // 0x0b5 'µ' 
              0, // 0x0b6 
             71, // 0x0b7 
              0, // 0x0b8 
             83, // 0x0b9 
             93, // 0x0ba 'º' 
            114, // 0x0bb 
            166, // 0x0bc 
            166, // 0x0bd 
            166, // 0x0be 
            102, // 0x0bf 
            124, // 0x0c0 'À' 
            124, // 0x0c1 'Á' 
            124, // 0x0c2 'Â' 
            124, // 0x0c3 'Ã' 
            124, // 0x0c4 'Ä' 
            124, // 0x0c5 'Å' 
            166, // 0x0c6 'Æ' 
            126, // 0x0c7 'Ç' 
            117, // 0x0c8 'È' 
            117, // 0x0c9 'É' 
            117, // 0x0ca 'Ê' 
            117, // 0x0cb 'Ë' 
             68, // 0x0cc 'Ì' 
             68, // 0x0cd 'Í' 
             68, // 0x0ce 'Î' 
             68, // 0x0cf 'Ï' 
            137, // 0x0d0 'Ð' 
            133, // 0x0d1 'Ñ' 
            139, // 0x0d2 'Ò' 
            139, // 0x0d3 'Ó' 
            139, // 0x0d4 'Ô' 
            139, // 0x0d5 'Õ' 
            139, // 0x0d6 'Ö' 
            147, // 0x0d7 
            139, // 0x0d8 'Ø' 
            131, // 0x0d9 'Ù' 
            131, // 0x0da 'Ú' 
            131, // 0x0db 'Û' 
            131, // 0x0dc 'Ü' 
            114, // 0x0dd 'Ý' 
            113, // 0x0de 'Þ' 
            116, // 0x0df 'ß' 
            114, // 0x0e0 'à' 
            114, // 0x0e1 'á' 
            114, // 0x0e2 'â' 
            114, // 0x0e3 'ã' 
            114, // 0x0e4 'ä' 
            114, // 0x0e5 'å' 
            167, // 0x0e6 'æ' 
            105, // 0x0e7 'ç' 
            114, // 0x0e8 'è' 
            114, // 0x0e9 'é' 
            114, // 0x0ea 'ê' 
            114, // 0x0eb 'ë' 
             65, // 0x0ec 'ì' 
             65, // 0x0ed 'í' 
             65, // 0x0ee 'î' 
             65, // 0x0ef 'ï' 
            114, // 0x0f0 'ð' 
            117, // 0x0f1 'ñ' 
            114, // 0x0f2 'ò' 
            114, // 0x0f3 'ó' 
            114, // 0x0f4 'ô' 
            114, // 0x0f5 'õ' 
            114, // 0x0f6 'ö' 
            147, // 0x0f7 
            114, // 0x0f8 'ø' 
            117, // 0x0f9 'ù' 
            117, // 0x0fa 'ú' 
            117, // 0x0fb 'û' 
            117, // 0x0fc 'ü' 
            111, // 0x0fd 'ý' 
            117, // 0x0fe 'þ' 
            111, // 0x0ff 'ÿ' 
            124, // 0x100 'Ā' 
            114, // 0x101 'ā' 
            124, // 0x102 'Ă' 
            114, // 0x103 'ă' 
            124, // 0x104 'Ą' 
            114, // 0x105 'ą' 
            126, // 0x106 'Ć' 
            105, // 0x107 'ć' 
            126, // 0x108 'Ĉ' 
            105, // 0x109 'ĉ' 
            126, // 0x10a 'Ċ' 
            105, // 0x10b 'ċ' 
            126, // 0x10c 'Č' 
            105, // 0x10d 'č' 
            137, // 0x10e 'Ď' 
            117, // 0x10f 'ď' 
            137, // 0x110 'Đ' 
            117, // 0x111 'đ' 
            117, // 0x112 'Ē' 
            114, // 0x113 'ē' 
            117, // 0x114 'Ĕ' 
            114, // 0x115 'ĕ' 
            117, // 0x116 'Ė' 
            114, // 0x117 'ė' 
            117, // 0x118 'Ę' 
            114, // 0x119 'ę' 
            117, // 0x11a 'Ě' 
            114, // 0x11b 'ě' 
            137, // 0x11c 'Ĝ' 
            117, // 0x11d 'ĝ' 
            137, // 0x11e 'Ğ' 
            117, // 0x11f 'ğ' 
            137, // 0x120 'Ġ' 
            117, // 0x121 'ġ' 
            137, // 0x122 'Ģ' 
            117, // 0x123 'ģ' 
            134, // 0x124 'Ĥ' 
            117, // 0x125 'ĥ' 
            158, // 0x126 'Ħ' 
            126, // 0x127 'ħ' 
             68, // 0x128 'Ĩ' 
             65, // 0x129 'ĩ' 
             68, // 0x12a 'Ī' 
             65, // 0x12b 'ī' 
             68, // 0x12c 'Ĭ' 
             65, // 0x12d 'ĭ' 
             68, // 0x12e 'Į' 
             65, // 0x12f 'į' 
             68, // 0x130 'İ' 
             65, // 0x131 'ı' 
            110, // 0x132 'Ĳ' 
            104, // 0x133 'ĳ' 
             68, // 0x134 'Ĵ' 
             65, // 0x135 'ĵ' 
            120, // 0x136 'Ķ' 
            109, // 0x137 'ķ' 
            109, // 0x138 'ĸ' 
            106, // 0x139 'Ĺ' 
             65, // 0x13a 'ĺ' 
            106, // 0x13b 'Ļ' 
             65, // 0x13c 'ļ' 
            106, // 0x13d 'Ľ' 
             79, // 0x13e 'ľ' 
            106, // 0x13f 'Ŀ' 
             75, // 0x140 'ŀ' 
            106, // 0x141 'Ł' 
             66, // 0x142 'ł' 
            133, // 0x143 'Ń' 
            117, // 0x144 'ń' 
            133, // 0x145 'Ņ' 
            117, // 0x146 'ņ' 
            133, // 0x147 'Ň' 
            117, // 0x148 'ň' 
            143, // 0x149 'ŉ' 
            133, // 0x14a 'Ŋ' 
            117, // 0x14b 'ŋ' 
            139, // 0x14c 'Ō' 
            114, // 0x14d 'ō' 
            139, // 0x14e 'Ŏ' 
            114, // 0x14f 'ŏ' 
            139, // 0x150 'Ő' 
            114, // 0x151 'ő' 
            180, // 0x152 'Œ' 
            173, // 0x153 'œ' 
            126, // 0x154 'Ŕ' 
             85, // 0x155 'ŕ' 
            126, // 0x156 'Ŗ' 
             85, // 0x157 'ŗ' 
            126, // 0x158 'Ř' 
             85, // 0x159 'ř' 
            117, // 0x15a 'Ś' 
            101, // 0x15b 'ś' 
            117, // 0x15c 'Ŝ' 
            101, // 0x15d 'ŝ' 
            117, // 0x15e 'Ş' 
            101, // 0x15f 'ş' 
            117, // 0x160 'Š' 
            101, // 0x161 'š' 
            114, // 0x162 'Ţ' 
             82, // 0x163 'ţ' 
            114, // 0x164 'Ť' 
             82, // 0x165 'ť' 
            114, // 0x166 'Ŧ' 
             82, // 0x167 'ŧ' 
            131, // 0x168 'Ũ' 
            117, // 0x169 'ũ' 
            131, // 0x16a 'Ū' 
            117, // 0x16b 'ū' 
            131, // 0x16c 'Ŭ' 
            117, // 0x16d 'ŭ' 
            131, // 0x16e 'Ů' 
            117, // 0x16f 'ů' 
            131, // 0x170 'Ű' 
            117, // 0x171 'ű' 
            131, // 0x172 'Ų' 
            117, // 0x173 'ų' 
            168, // 0x174 'Ŵ' 
            144, // 0x175 'ŵ' 
            114, // 0x176 'Ŷ' 
            111, // 0x177 'ŷ' 
            114, // 0x178 'Ÿ' 
            124, // 0x179 'Ź' 
            101, // 0x17a 'ź' 
            124, // 0x17b 'Ż' 
            101, // 0x17c 'ż' 
            124, // 0x17d 'Ž' 
            101, // 0x17e 'ž' 
             76, // 0x17f 'ſ' 
            117, // 0x180 'ƀ' 
            132, // 0x181 'Ɓ' 
            124, // 0x182 'Ƃ' 
            117, // 0x183 'ƃ' 
            124, // 0x184 'Ƅ' 
            117, // 0x185 'ƅ' 
            127, // 0x186 'Ɔ' 
            126, // 0x187 'Ƈ' 
            105, // 0x188 'ƈ' 
            137, // 0x189 'Ɖ' 
            144, // 0x18a 'Ɗ' 
            124, // 0x18b 'Ƌ' 
            117, // 0x18c 'ƌ' 
            114, // 0x18d 'ƍ' 
            116, // 0x18e 'Ǝ' 
            139, // 0x18f 'Ə' 
            114, // 0x190 'Ɛ' 
            108, // 0x191 'Ƒ' 
             76, // 0x192 'ƒ' 
            137, // 0x193 'Ɠ' 
            126, // 0x194 'Ɣ' 
            168, // 0x195 'ƕ' 
             76, // 0x196 'Ɩ' 
             68, // 0x197 'Ɨ' 
            133, // 0x198 'Ƙ' 
            109, // 0x199 'ƙ' 
             65, // 0x19a 'ƚ' 
            110, // 0x19b 'ƛ' 
            169, // 0x19c 'Ɯ' 
            133, // 0x19d 'Ɲ' 
            117, // 0x19e 'ƞ' 
            139, // 0x19f 'Ɵ' 
            157, // 0x1a0 'Ơ' 
            114, // 0x1a1 'ơ' 
            162, // 0x1a2 'Ƣ' 
            135, // 0x1a3 'ƣ' 
            120, // 0x1a4 'Ƥ' 
            117, // 0x1a5 'ƥ' 
            126, // 0x1a6 'Ʀ' 
            117, // 0x1a7 'Ƨ' 
            101, // 0x1a8 'ƨ' 
            117, // 0x1a9 'Ʃ' 
             74, // 0x1aa 'ƪ' 
             82, // 0x1ab 'ƫ' 
            114, // 0x1ac 'Ƭ' 
             82, // 0x1ad 'ƭ' 
            114, // 0x1ae 'Ʈ' 
            149, // 0x1af 'Ư' 
            117, // 0x1b0 'ư' 
            136, // 0x1b1 'Ʊ' 
            130, // 0x1b2 'Ʋ' 
            133, // 0x1b3 'Ƴ' 
            131, // 0x1b4 'ƴ' 
            124, // 0x1b5 'Ƶ' 
            101, // 0x1b6 'ƶ' 
            122, // 0x1b7 'Ʒ' 
            122, // 0x1b8 'Ƹ' 
            109, // 0x1b9 'ƹ' 
            101, // 0x1ba 'ƺ' 
            117, // 0x1bb 
            120, // 0x1bc 'Ƽ' 
            109, // 0x1bd 'ƽ' 
             99, // 0x1be 'ƾ' 
            117, // 0x1bf 'ƿ' 
             68, // 0x1c0 
             96, // 0x1c1 
             92, // 0x1c2 
             68, // 0x1c3 
            231, // 0x1c4 'Ǆ' 
            213, // 0x1c5 'ǅ' 
            192, // 0x1c6 'ǆ' 
            146, // 0x1c7 'Ǉ' 
            139, // 0x1c8 'ǈ' 
             91, // 0x1c9 'ǉ' 
            160, // 0x1ca 'Ǌ' 
            159, // 0x1cb 'ǋ' 
            141, // 0x1cc 'ǌ' 
            124, // 0x1cd 'Ǎ' 
            114, // 0x1ce 'ǎ' 
             68, // 0x1cf 'Ǐ' 
             65, // 0x1d0 'ǐ' 
            139, // 0x1d1 'Ǒ' 
            114, // 0x1d2 'ǒ' 
            131, // 0x1d3 'Ǔ' 
            117, // 0x1d4 'ǔ' 
            131, // 0x1d5 'Ǖ' 
            117, // 0x1d6 'ǖ' 
            131, // 0x1d7 'Ǘ' 
            117, // 0x1d8 'ǘ' 
            131, // 0x1d9 'Ǚ' 
            117, // 0x1da 'ǚ' 
            131, // 0x1db 'Ǜ' 
            117, // 0x1dc 'ǜ' 
            114, // 0x1dd 'ǝ' 
            124, // 0x1de 'Ǟ' 
            114, // 0x1df 'ǟ' 
            124, // 0x1e0 'Ǡ' 
            114, // 0x1e1 'ǡ' 
            166, // 0x1e2 'Ǣ' 
            167, // 0x1e3 'ǣ' 
            137, // 0x1e4 'Ǥ' 
            117, // 0x1e5 'ǥ' 
            137, // 0x1e6 'Ǧ' 
            117, // 0x1e7 'ǧ' 
            120, // 0x1e8 'Ǩ' 
            109, // 0x1e9 'ǩ' 
            139, // 0x1ea 'Ǫ' 
            114, // 0x1eb 'ǫ' 
            139, // 0x1ec 'Ǭ' 
            114, // 0x1ed 'ǭ' 
            122, // 0x1ee 'Ǯ' 
            109, // 0x1ef 'ǯ' 
             65, // 0x1f0 'ǰ' 
            231, // 0x1f1 'Ǳ' 
            213, // 0x1f2 'ǲ' 
            192, // 0x1f3 'ǳ' 
            137, // 0x1f4 'Ǵ' 
            117, // 0x1f5 'ǵ' 
            186, // 0x1f6 'Ƕ' 
            124, // 0x1f7 'Ƿ' 
            133, // 0x1f8 'Ǹ' 
            117, // 0x1f9 'ǹ' 
            124, // 0x1fa 'Ǻ' 
            114, // 0x1fb 'ǻ' 
            166, // 0x1fc 'Ǽ' 
            167, // 0x1fd 'ǽ' 
            139, // 0x1fe 'Ǿ' 
            114, // 0x1ff 'ǿ' 
            124, // 0x200 'Ȁ' 
            114, // 0x201 'ȁ' 
            124, // 0x202 'Ȃ' 
            114, // 0x203 'ȃ' 
            117, // 0x204 'Ȅ' 
            114, // 0x205 'ȅ' 
            117, // 0x206 'Ȇ' 
            114, // 0x207 'ȇ' 
             68, // 0x208 'Ȉ' 
             65, // 0x209 'ȉ' 
             68, // 0x20a 'Ȋ' 
             65, // 0x20b 'ȋ' 
            139, // 0x20c 'Ȍ' 
            114, // 0x20d 'ȍ' 
            139, // 0x20e 'Ȏ' 
            114, // 0x20f 'ȏ' 
            126, // 0x210 'Ȑ' 
             85, // 0x211 'ȑ' 
            126, // 0x212 'Ȓ' 
             85, // 0x213 'ȓ' 
            131, // 0x214 'Ȕ' 
            117, // 0x215 'ȕ' 
            131, // 0x216 'Ȗ' 
            117, // 0x217 'ȗ' 
            117, // 0x218 'Ș' 
            101, // 0x219 'ș' 
            114, // 0x21a 'Ț' 
             82, // 0x21b 'ț' 
            116, // 0x21c 'Ȝ' 
            101, // 0x21d 'ȝ' 
            134, // 0x21e 'Ȟ' 
            117, // 0x21f 'ȟ' 
            132, // 0x220 'Ƞ' 
            147, // 0x221 'ȡ' 
            126, // 0x222 'Ȣ' 
            110, // 0x223 'ȣ' 
            124, // 0x224 'Ȥ' 
            101, // 0x225 'ȥ' 
            124, // 0x226 'Ȧ' 
            114, // 0x227 'ȧ' 
            117, // 0x228 'Ȩ' 
            114, // 0x229 'ȩ' 
            139, // 0x22a 'Ȫ' 
            114, // 0x22b 'ȫ' 
            139, // 0x22c 'Ȭ' 
            114, // 0x22d 'ȭ' 
            139, // 0x22e 'Ȯ' 
            114, // 0x22f 'ȯ' 
            139, // 0x230 'Ȱ' 
            114, // 0x231 'ȱ' 
            114, // 0x232 'Ȳ' 
            111, // 0x233 'ȳ' 
             94, // 0x234 'ȴ' 
            147, // 0x235 'ȵ' 
             94, // 0x236 'ȶ' 
        };

	public Table(){
	    super();
	}

	public int em(){
	    return Table.Em;
	}
	public int height(){
	    return Table.Height;
	}
	public int width(String string){
	    string = LayoutFont.Normalize(string);

	    char[] cary = string.toCharArray();
	    int w = 0;
	    for (char ch: cary)
		w += this.width(ch);
	    return w;
	}
	public int width(char ch){
	    return this.width( (int)ch);
	}
	public int width(int ch){
	    if (-1 < ch && ch < Table.Widths.length)
		return Table.Widths[ch];
	    else
		return 0;
	}
    }




    protected LayoutFont(){
	super();
    }


    public abstract int em();

    /*
     * This value should be "105" for the "leading" line touching the
     * top of a string of caps, eg, "ABC", and the "ascent" line
     * touching the bottom -- as seen in "LayoutFont$Test".
     * 
     * In this case, we've got a font to approximate what we see in
     * GSCHEM -- and can use to calculate string widths.
     */
    public abstract int height();

    public abstract int width(String string);

    public abstract int width(char ch);


    public static String Normalize(String s){

	if (s.startsWith("\\_") && s.endsWith("\\_"))
	    return s.substring(2,s.length()-2);
	else
	    return s;
    }

    public static class TableWriter {

	public static void main(String[] argv){

	    final LayoutFont.Soft layout = new LayoutFont.Soft();

	    System.out.printf("        private final static int Height = %d;%n",layout.height);
	    System.out.printf("        private final static int Ascent = %d;%n",layout.ascent);
	    System.out.printf("        private final static int Leading = %d;%n",layout.leading);
	    System.out.printf("        private final static int Em = %d;%n",layout.em);

	    System.out.printf("        private final static int[] Widths = {%n");
	    for (char ch = 0x0; ch < 0x237; ch++){
		int tt = Character.getType(ch);
		switch(tt){
		case 0:
		    System.out.printf("            %3d, // 0x%03x %n",0,(int)ch);
		    break;
		case 1:
		case 2:
		case 3:
		case 9:
		    System.out.printf("            %3d, // 0x%03x '%c' %n",layout.width(ch),(int)ch, ch);
		    break;
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 27:
		case 28:
		    System.out.printf("            %3d, // 0x%03x %n",0,(int)ch);
		    break;
		default:
		    if (0xFFFFFFFF != tt)
			System.out.printf("            %3d, // 0x%03x %n",layout.width(ch),(int)ch);
		    else
			System.out.printf("            %3d, // 0x%03x %n",0,(int)ch);
		    break;
		}
	    }
	    System.out.printf("        };%n");
	}
    }
    public static class Test
	extends javax.swing.JFrame
    {
	private final String string;

	private final Font msg = new Font(Font.MONOSPACED,Font.BOLD,24);

	private final Color ok = new Color(0x10,0xaf,0x10);
	private final Color er = new Color(0xaf,0x10,0x10);

	private final int width, height;

	private final LayoutFont.Soft instance = new LayoutFont.Soft();


	public Test(String[] argv){
	    super("test");
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.setBackground(java.awt.Color.white);
	    this.setForeground(java.awt.Color.blue);
	    this.setSize(600,600);

	    final Toolkit tk = Toolkit.getDefaultToolkit();

	    this.string = argv[0];
	    this.width = this.instance.width(this.string);
	    this.height = this.instance.height();

	    java.awt.Dimension screen = tk.getScreenSize();

	    int px = (screen.width>>1)-600;
	    int py = (screen.height>>1)-600;

	    this.setLocation(px,py);
	    this.show();

	    System.out.printf("%s (%d x %d)%n",this.string,this.width,this.height);
	}

	public void paint(java.awt.Graphics g) {
	    this.paint( (java.awt.Graphics2D) g);
	}
	public void update(java.awt.Graphics g) {
	    this.paint( (java.awt.Graphics2D) g);
	}
	private void paint(java.awt.Graphics2D g) {

	    int x = 10, y = 200;

	    final java.awt.Dimension thisSize = this.getSize();
	    final java.awt.Insets thisInsets = this.getInsets();
	    {
		x += thisInsets.left+10;
		y += thisInsets.top+10;
		thisSize.width -= (thisInsets.left+thisInsets.right);
		thisSize.height -= (thisInsets.top+thisInsets.bottom);
	    }
	    g.setColor(this.getBackground());
	    {
		g.fillRect(thisInsets.top, thisInsets.left, thisSize.width, thisSize.height);
	    }
	    g.setColor(this.getForeground());
	    /*
	     * Use Layout Font
	     */
	    g.setFont(this.instance.font);

	    g.drawString(this.string,x,y);

	    g.setColor(Color.red);

	    Rectangle r = new Rectangle(x,(y-this.height),this.width,this.height);

	    g.drawRect(r.x,r.y,r.width,r.height);

	    if (105 == this.height){

		g.setColor(this.ok);
		g.setFont(this.msg);

		g.drawString("OK 105",400,80);
	    }
	    else {

		g.setColor(this.er);
		g.setFont(this.msg);

		g.drawString(String.format("ER %d",this.height),400,80);
	    }
	}


	public static void main(String[] argv){

	    if (0 < argv.length)
		new Test(argv);
	    else {
		System.err.println("Usage" );
		System.err.println();
		System.err.println("  java -cp sch.jar libra.LayoutFont$Test <string>" );
		System.err.println();
		System.exit(1);
	    }
	}
    }
}

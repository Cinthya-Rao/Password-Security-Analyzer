package service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DictionaryChecker {
    private final Set<String> commonPasswords = new HashSet<>(Arrays.asList(
            "123456","123456789","12345","qwerty","password","12345678","111111","123123","1234567890","1234567",
            "qwerty123","000000","1q2w3e","aa12345678","abc123","password1","1234","qwertyuiop","123321","password123",
            "1q2w3e4r5t","iloveyou","654321","666666","987654321","123","123456a","qwe123","1q2w3e4r","7777777",
            "1qaz2wsx","123qwe","zxcvbnm","121212","asdasd","a123456","555555","dragon","112233","123123123",
            "monkey","11111111","qazwsx","159753","asdfghjkl","222222","1234qwer","qwerty1","123654","123abc",
            "admin","welcome","letmein","football","baseball","master","sunshine","shadow","superman","princess",
            "trustno1","login","passw0rd","starwars","whatever","hello","freedom","charlie","donald","michael",
            "jordan","hunter","buster","soccer","harley","batman","andrew","tigger","pepper","george",
            "summer","winter","spring","autumn","flower","hannah","thomas","robert","matrix","computer",
            "internet","server","security","secret","qwerty12","qwerty12345","zaq12wsx","!qaz2wsx","q1w2e3r4",
            "abcd1234","1qazxsw2","pass123","password12","password1234","p@ssword","p@ssw0rd","admin123","adminadmin","root",
            "toor","administrator","welcome1","welcome123","changeme","default","guest","guest123","user","user123",
            "test","test123","testing","demo","demo123","sample","sample123","temp","temporary","unknown",
            "qweasdzxc","asdf1234","asdfasdf","qwer1234","qwerasdf","zxcv1234","poiuytrewq","mnbvcxz","lkjhgfdsa","qazwsxedc",
            "lovely","loveme","loveyou","iloveu","ilovegod","godisgood","jesus","angel","killer","pokemon",
            "naruto","blink182","metallica","slipknot","mustang","ferrari","mercedes","chevrolet","corvette","jaguar",
            "liverpool","chelsea","arsenal","manutd","yankees","cowboys","dallas","lakers","rangers","packers",
            "college","student","teacher","school","homework","letmein1","opensesame","access","access123","secure",
            "secure123","mypassword","newpassword","oldpassword","password2024","password2025","password2026","january","february","march",
            "april","maymay","june","july","august","september","october","november","december","monday",
            "tuesday","wednesday","thursday","friday","saturday","sunday","coffee","cookie","banana","orange",
            "purple","yellow","silver","golden","blue","green","redred","black","white","brown",
            "michelle","daniel","ashley","jennifer","jessica","william","qwert","asdf","zxcv","aaaaaa",
            "aaaaaaa","aaaaaaaa","1111111","999999","888888","777777","696969","131313","101010","147258369",
            "25802580","852456","741852963","963852741","147852369","123098","102030","11223344","12344321","4321",
            "87654321","987654","7654321","246810","13579","abcdef","abcde","abcdefg","abcdefgh","abcabc",
            "testtest","passwordpassword","superuser","poweruser","operator","oracle","mysql","postgres","docker","kubernetes",
            "github","gitlab","bitbucket","codex","openai","chatgpt","linkedin","facebook","instagram","twitter",
            "snapchat","tiktok","netflix","spotify","amazon","google","youtube","paypal","banking","account",
            "qwerty2024","qwerty2025","qwerty2026","summer2024","summer2025","summer2026","winter2024","winter2025","winter2026","india123",
            "america","america1","newyork","london","paris","sydney","dragon123","monkey123","football1","baseball1"
    ));

    private final Set<String> commonWords = new HashSet<>(Arrays.asList(
            "password", "admin", "welcome", "login", "secret", "football", "baseball", "dragon", "monkey",
            "letmein", "qwerty", "iloveyou", "sunshine", "princess", "security", "computer", "internet",
            "summer", "winter", "spring", "autumn", "company", "school", "student", "default", "guest"
    ));

    public boolean isCommonPassword(String password) {
        if (password == null) {
            return false;
        }
        String lower = password.toLowerCase(Locale.ROOT);
        return commonPasswords.contains(lower) || commonPasswords.contains(normalize(password));
    }

    public boolean containsCommonWord(String password) {
        if (password == null) {
            return false;
        }
        String normalized = normalize(password);
        return commonWords.stream().anyMatch(normalized::contains);
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT)
                .replace("@", "a")
                .replace("$", "s")
                .replace("0", "o")
                .replace("1", "i")
                .replace("3", "e")
                .replace("5", "s")
                .replace("7", "t");
    }
}

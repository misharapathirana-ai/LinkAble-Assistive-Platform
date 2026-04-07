# post_process.py

# Map standalone swara → vowel sign
SWARA_TO_SIGN = {
    "ආ": "ා",
    "ඉ": "ි",
    "ඊ": "ී",
    "උ": "ු",
    "ඌ": "ූ",
    "එ": "ෙ",
    "ඒ": "ේ",
    "ඔ": "ො",
    "ඕ": "ෝ",
    "ඇ": "ැ",
    "ඈ": "ෑ",
    "ඓ": "ෛ",
    "ෞ": "ෞ",  
}

# Sinhala consonants (from JSON)
VYANJANA = set([
    "ක","ග","ජ","ට","ද","ණ","ත","ඩ","න","ප","බ","ම","ය","ෆ","ථ","ර","ල","ව",
    "ස","හ","ශ","ෂ","ධ","ඨ","ච","ඪ","ඵ","ළ","ඥ","ඟ","ඬ","ඳ","ඹ","ඛ","ඝ","ඣ"
])

COMMON_WORD_FIXES = {
    
     # Core family words
    "අමමා": "අම්මා",
    "තාතතා": "තාත්තා",
    "අයයා": "අයියා",
    "අකකා": "අක්කා",
    "මලලි": "මල්ලි",

    # Very common verbs 
    "යනන": "යන්න",
    "එනන": "එන්න",
    "බලනන": "බලන්න",
    "කියනන": "කියන්න",
    "ගනන": "ගන්න",
    "දෙනන": "දෙන්න",
    "කරනන": "කරන්න",
    "හිතනන": "හිතන්න",
    "ඉනන": "ඉන්න",
    "දකිනන": "දකින්න",

    # Question / casual speech forms
    "එනනද": "එන්නද",
    "යනනද": "යන්නද",
    "කරනනද": "කරන්නද",
    "බලනනද": "බලන්නද",

    # Casual usage
    "කරනනේ": "කරන්නෙ",
    "යනනේ": "යන්නෙ",
    "එනනෙ": "එන්නෙ",
    "අනන": "අන්න",  
    "හොද": "හොඳ",
    "ගොඩක": "ගොඩක්",
    "නැදද":"නැද්ද",

}

def post_process_text(text: str) -> str:
    words = text.split()
    processed_words = []

    for word in words:
        i = 0
        new_word = ""

        while i < len(word) - 1:
            current_char = word[i]
            next_char = word[i + 1]

            # Layer 1: Vyanjana + Swara → combine
            if current_char in VYANJANA and next_char in SWARA_TO_SIGN:
                combined = current_char + SWARA_TO_SIGN[next_char]
                new_word += combined
                i += 2
            else:
                new_word += current_char
                i += 1

        if i == len(word) - 1:
            new_word += word[i]   # add last char if not processed

        
        
        # Layer 2: Dictionary fix (hal kirima & common words)
        fixed_word = COMMON_WORD_FIXES.get(new_word, new_word)

        processed_words.append(fixed_word)

    return " ".join(processed_words)


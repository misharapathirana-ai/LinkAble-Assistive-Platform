# pre_process.py

# Map vowel signs → standalone swara
SIGN_TO_SWARA = {
    "ා": "ආ",
    "ි": "ඉ",
    "ී": "ඊ",
    "ු": "උ",
    "ූ": "ඌ",
    "ෙ": "එ",
    "ේ": "ඒ",
    "ො": "ඔ",
    "ෝ": "ඕ",
    "ැ": "ඇ",
    "ෑ": "ඈ",
    "ෛ": "ෛ",
    "ෞ": "ෞ",
}

# Sinhala consonants (base letters)
VYANJANA = set([
    "ක","ග","ජ","ට","ද","ණ","ත","ඩ","න","ප","බ","ම","ය","ෆ","ථ","ර","ල","ව",
    "ස","හ","ශ","ෂ","ධ","ඨ","ච","ඪ","ඵ","ළ","ඥ","ඟ","ඬ","ඳ","ඹ","ඛ","ඝ","ඣ"
])

def pre_process_text(text: str) -> str:

    result = ""
    i = 0

    while i < len(text):
        char = text[i]

        # If consonant followed by vowel sign → split
        if i + 1 < len(text) and char in VYANJANA:
            next_char = text[i + 1]
            if next_char in SIGN_TO_SWARA:
                result += char + SIGN_TO_SWARA[next_char]
                i += 2
                continue

        # Otherwise keep char
        result += char
        i += 1

    return result

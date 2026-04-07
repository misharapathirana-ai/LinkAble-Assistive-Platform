import os
from PIL import Image

def text_to_sign_image(text: str, reverse_mapping: dict, output_path="output.png"):
    text = text.rstrip()
    if not text:
        return None

    paragraphs = text.split("\n")  # <-- real user line breaks

    # Build list of images per word
    structured_words = []
    for paragraph in paragraphs:
        line_words = []

        for word in paragraph.split():
            letters = []
            for char in word:
                sign_file = reverse_mapping.get(char) or reverse_mapping.get(char.lower())

                if sign_file:
                    img_path = os.path.join("signs", sign_file)
                    if os.path.exists(img_path):
                        try:
                            img = Image.open(img_path).convert("RGBA")
                            letters.append(img)
                        except Exception:
                            pass

            if letters:
                line_words.append(letters)

        structured_words.append(line_words)

    if not any(structured_words):
        return None

    # ──────────────────────────────────────
    #           LAYOUT SETTINGS 
    # ──────────────────────────────────────
    TARGET_HEIGHT    = 160
    LETTER_GAP       = 10
    WORD_GAP         = 60
    LINE_GAP         = 90
    SIDE_PADDING     = 80
    VERTICAL_PADDING = 80
    ABSOLUTE_MAX_WIDTH = 3000

    char_count = sum(len(w) for w in text.split())
    MAX_LINE_WIDTH = min(1200 + char_count * 35, ABSOLUTE_MAX_WIDTH)

    def resize_and_pad(img):
        w, h = img.size
        scale = TARGET_HEIGHT / h
        new_w = int(w * scale)
        resized = img.resize((new_w, TARGET_HEIGHT), Image.Resampling.LANCZOS)

        PAD = 10
        final_w = new_w + PAD * 2
        final_h = TARGET_HEIGHT + PAD * 2
        padded = Image.new("RGBA", (final_w, final_h), (0, 0, 0, 0))
        padded.paste(resized, (PAD, PAD), resized)
        return padded, final_w

    # ────────────────────────────────────────────────
    # Build lines (NORMAL wrapping + FORCED breaks)
    # ────────────────────────────────────────────────
    lines = []

    for paragraph in structured_words:

        processed_words = []
        for letters in paragraph:
            resized_letters = []
            word_total_w = 0
            for img in letters:
                padded_img, w = resize_and_pad(img)
                resized_letters.append(padded_img)
                word_total_w += w + LETTER_GAP
            word_total_w -= LETTER_GAP
            processed_words.append((resized_letters, word_total_w))

        current_line = []
        current_w = 0

        for letters, w in processed_words:
            added = w if not current_line else w + WORD_GAP

            if current_w + added > MAX_LINE_WIDTH - SIDE_PADDING * 2:
                if current_line:
                    lines.append(current_line)
                current_line = [(letters, w)]
                current_w = w
            else:
                current_line.append((letters, w))
                current_w += added

        if current_line:
            lines.append(current_line)

        
        # user pressed ENTER → force new line, no extra spacing
        lines.append(None)

    if lines and lines[-1] is None:
        lines.pop()

    if not lines:
        return None

    # width calc
    max_used_width = 0
    visual_lines = [l for l in lines if l is not None]

    for line in visual_lines:
        line_w = sum(w for _, w in line) + WORD_GAP * max(0, len(line) - 1)
        max_used_width = max(max_used_width, line_w)

    canvas_width = min(max_used_width + SIDE_PADDING * 2, MAX_LINE_WIDTH + 100)

    sign_h = TARGET_HEIGHT + 40
    canvas_height = VERTICAL_PADDING * 2 + len(visual_lines) * sign_h + LINE_GAP * max(0, len(visual_lines) - 1)

    final_img = Image.new("RGB", (canvas_width, canvas_height), (255, 255, 255))

    # draw
    y = VERTICAL_PADDING
    for line in lines:

        if line is None:
            continue  # just next line (no extra space)

        line_w = sum(w for _, w in line) + WORD_GAP * max(0, len(line) - 1)
        x = (canvas_width - line_w) // 2

        for letters, _ in line:
            for img in letters:
                final_img.paste(img, (x, y), img)
                x += img.width + LETTER_GAP
            x += WORD_GAP

        y += sign_h + LINE_GAP

    try:
        final_img.save(output_path)
        return output_path
    except Exception as e:
        print("Save failed:", e)
        return None

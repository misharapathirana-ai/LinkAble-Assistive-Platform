import json
import re
from telegram import Update
from telegram.ext import ApplicationBuilder, CommandHandler, MessageHandler, ContextTypes, filters

from post_process import post_process_text
from pre_process import pre_process_text
from image_generator import text_to_sign_image


# Load Mappings

with open("mapping.json", "r", encoding="utf-8") as f:
    MAPPING = json.load(f)

with open("reverse_mapping.json", "r", encoding="utf-8") as f:
    REVERSE_MAPPING = json.load(f)


# Telegram Command Handlers

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "Hi! 👋\n"
        "I'm here to help you with the translation."
    )


# Core Translation Handler

async def translate(update: Update, context: ContextTypes.DEFAULT_TYPE):
    text = update.message.text.rstrip()   # keep line breaks
    print("Received:", text)

    # CASE 1: Sign IDs → Text
    if "ssl_" in text:

        lines = text.split("\n")
        final_lines = []

        for line in lines:
            words = line.split()
            translated_words = []

            for word in words:
                codes = re.findall(r'ssl_si_\d{3}|ssl_\d{3}', word)
                translated_chars = []

                for code in codes:
                    if code in MAPPING:
                        translated_chars.append(MAPPING[code])
                    else:
                        translated_chars.append(f"[{code}]")

                translated_word = "".join(translated_chars)
                translated_words.append(translated_word)

            raw_result = " ".join(translated_words)

            if raw_result.strip():
                final_lines.append(post_process_text(raw_result))
            else:
                final_lines.append("")

        final_result = "\n".join(final_lines)

        if not final_result.strip():
            final_result = "❗ I couldn't recognize any sign IDs in that message."

        await update.message.reply_text(final_result)
        

    # CASE 2: Text → Sign Image
    else:
        normalized_text = pre_process_text(text)

        output_image_path = "output.png"
        img_path = text_to_sign_image(normalized_text, REVERSE_MAPPING, output_image_path)

        if img_path is None:
            await update.message.reply_text("❗ I couldn't generate signs for that text.")
        else:
            with open(img_path, "rb") as img_file:
                await update.message.reply_photo(photo=img_file)


# Bot Startup

def main():
    TOKEN = "8324030882:AAFjdqUzOmABIQqxI5jVqmcGIkYvTQAMfYM"

    app = ApplicationBuilder().token(TOKEN).build()

    app.add_handler(CommandHandler("start", start))
    app.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, translate))

    print("Bot is running...")
    app.run_polling(drop_pending_updates=True)


if __name__ == "__main__":
    main()

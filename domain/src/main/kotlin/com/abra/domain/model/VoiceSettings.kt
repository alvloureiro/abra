package com.abra.domain.model

data class VoiceSettings(
    val language: LanguageOption = LanguageOption.ENGLISH,
    val voiceProfile: VoiceProfile = VoiceProfile.SYSTEM,
    val voiceId: String? = null,
)

enum class LanguageOption(
    val tag: String,
    val displayName: String,
) {
    ENGLISH("en-US", "English"),
    PORTUGUESE_BR("pt-BR", "Portuguese-BR"),
    ;

    companion object {
        fun fromTag(tag: String?): LanguageOption = entries.firstOrNull { it.tag == tag } ?: ENGLISH
    }
}

enum class VoiceProfile(
    val displayName: String,
) {
    SYSTEM("System"),
    FEMALE("Female"),
    MALE("Male"),
    NEUTRAL("Neutral"),
}

data class VoiceOption(
    val id: String,
    val name: String,
    val language: LanguageOption,
    val profile: VoiceProfile,
    val requiresNetwork: Boolean,
)

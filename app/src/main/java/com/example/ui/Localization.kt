package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

enum class AppLanguage(val code: String, val displayName: String, val isRtl: Boolean) {
    ENGLISH("en", "English", false),
    ARABIC("ar", "العربية", true),
    FRENCH("fr", "Français", false)
}

object Localization {
    private val translations = mapOf(
        "app_name" to mapOf(
            AppLanguage.ENGLISH to "Haris Family Safety",
            AppLanguage.ARABIC to "حارس لسلامة العائلة",
            AppLanguage.FRENCH to "Haris Sécurité Familiale"
        ),
        "app_title" to mapOf(
            AppLanguage.ENGLISH to "Family Companion",
            AppLanguage.ARABIC to "مرافق العائلة",
            AppLanguage.FRENCH to "Compagnon Familial"
        ),
        "app_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Parental Management Dashboard",
            AppLanguage.ARABIC to "لوحة التحكم والإدارة الأبوية",
            AppLanguage.FRENCH to "Tableau de Bord Parental"
        ),
        "tagline" to mapOf(
            AppLanguage.ENGLISH to "Complete Algerian Parent & Child Security Companion",
            AppLanguage.ARABIC to "المرافق الشامل لحماية الآباء والأبناء في الجزائر",
            AppLanguage.FRENCH to "Le compagnon complet de sécurité pour parents et enfants"
        ),
        "who_using" to mapOf(
            AppLanguage.ENGLISH to "Who is using this device right now?",
            AppLanguage.ARABIC to "من يستخدم هذا الجهاز حالياً؟",
            AppLanguage.FRENCH to "Qui utilise cet appareil actuellement ?"
        ),
        "i_am_parent" to mapOf(
            AppLanguage.ENGLISH to "I am a Parent",
            AppLanguage.ARABIC to "أنا أب / أم",
            AppLanguage.FRENCH to "Je suis un Parent"
        ),
        "parent_desc" to mapOf(
            AppLanguage.ENGLISH to "Configure schedules, inspect suspicious logs, geofence school safe points, and converse with our AI safety coach.",
            AppLanguage.ARABIC to "قم بضبط الجداول الزمنية، وفحص السجلات المشبوهة، وتحديد الموقع الجغرافي للمدرسة، والتحدث مع مدرب السلامة الذكي.",
            AppLanguage.FRENCH to "Configurez les horaires, inspectez les journaux suspects, géofencez l'école et discutez avec notre conseiller IA."
        ),
        "i_am_child" to mapOf(
            AppLanguage.ENGLISH to "I am a Child",
            AppLanguage.ARABIC to "أنا طفل",
            AppLanguage.FRENCH to "Je suis un Enfant"
        ),
        "child_desc" to mapOf(
            AppLanguage.ENGLISH to "Connect this phone as a background sentinel transmitting location, active screens, and search terms.",
            AppLanguage.ARABIC to "قم بربط هذا الهاتف كحارس في الخلفية يرسل الموقع الجغرافي، الشاشات النشطة، وكلمات البحث.",
            AppLanguage.FRENCH to "Connectez ce téléphone comme sentinelle envoyant la position, les écrans actifs et les termes recherchés."
        ),
        "regulation_compliant" to mapOf(
            AppLanguage.ENGLISH to "Algeria Families Safety Regulation Compliant",
            AppLanguage.ARABIC to "متوافق مع لوائح سلامة الأسر الجزائرية",
            AppLanguage.FRENCH to "Conforme à la réglementation algérienne sur la sécurité des familles"
        ),
        "overview" to mapOf(
            AppLanguage.ENGLISH to "Overview",
            AppLanguage.ARABIC to "نظرة عامة",
            AppLanguage.FRENCH to "Aperçu"
        ),
        "rules" to mapOf(
            AppLanguage.ENGLISH to "Rules",
            AppLanguage.ARABIC to "القواعد والحدود",
            AppLanguage.FRENCH to "Règles"
        ),
        "location" to mapOf(
            AppLanguage.ENGLISH to "Location",
            AppLanguage.ARABIC to "الموقع الجغرافي",
            AppLanguage.FRENCH to "Localisation"
        ),
        "ai_coach" to mapOf(
            AppLanguage.ENGLISH to "AI Coach",
            AppLanguage.ARABIC to "مستشار الذكاء الاصطناعي",
            AppLanguage.FRENCH to "Conseiller IA"
        ),
        "switch_roles" to mapOf(
            AppLanguage.ENGLISH to "Switch Roles",
            AppLanguage.ARABIC to "تبديل الأدوار",
            AppLanguage.FRENCH to "Changer de rôle"
        ),
        "emergency_pause" to mapOf(
            AppLanguage.ENGLISH to "Emergency Remote Lockdown",
            AppLanguage.ARABIC to "قفل الأجهزة في الطوارئ",
            AppLanguage.FRENCH to "Verrouillage d'urgence"
        ),
        "device_paused_title" to mapOf(
            AppLanguage.ENGLISH to "Device Pause Mode",
            AppLanguage.ARABIC to "وضع إيقاف الأجهزة",
            AppLanguage.FRENCH to "Mode Pause de l'appareil"
        ),
        "device_paused_desc" to mapOf(
            AppLanguage.ENGLISH to "Do you wish to instantly PAUSE ALL children's devices? This blocks screen access on all connected devices until manually unlocked.",
            AppLanguage.ARABIC to "هل تريد إيقاف جميع أجهزة الأطفال فوراً؟ سيؤدي هذا لحظر الوصول إلى الشاشات حتى يتم إلغاء القفل يدوياً.",
            AppLanguage.FRENCH to "Voulez-vous suspendre instantanément tous les appareils des enfants ? Cela bloquera l'accès aux écrans jusqu'au déverrouillage manuel."
        ),
        "pause_all_now" to mapOf(
            AppLanguage.ENGLISH to "PAUSE ALL NOW",
            AppLanguage.ARABIC to "إيقاف الجميع الآن",
            AppLanguage.FRENCH to "TOUT SUSPENDRE"
        ),
        "cancel" to mapOf(
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.ARABIC to "إلغاء",
            AppLanguage.FRENCH to "Annuler"
        ),
        "done" to mapOf(
            AppLanguage.ENGLISH to "Done",
            AppLanguage.ARABIC to "تم",
            AppLanguage.FRENCH to "Terminé"
        ),
        "theme" to mapOf(
            AppLanguage.ENGLISH to "Theme",
            AppLanguage.ARABIC to "المظهر",
            AppLanguage.FRENCH to "Thème"
        ),
        "language" to mapOf(
            AppLanguage.ENGLISH to "Language",
            AppLanguage.ARABIC to "اللغة",
            AppLanguage.FRENCH to "Langue"
        ),
        "light_mode" to mapOf(
            AppLanguage.ENGLISH to "Light Mode",
            AppLanguage.ARABIC to "الوضع المضيء",
            AppLanguage.FRENCH to "Mode Clair"
        ),
        "dark_mode" to mapOf(
            AppLanguage.ENGLISH to "Dark Mode",
            AppLanguage.ARABIC to "الوضع المظلم",
            AppLanguage.FRENCH to "Mode Sombre"
        ),
        "active" to mapOf(
            AppLanguage.ENGLISH to "Active",
            AppLanguage.ARABIC to "نشط",
            AppLanguage.FRENCH to "Actif"
        ),
        "paused" to mapOf(
            AppLanguage.ENGLISH to "Paused",
            AppLanguage.ARABIC to "موقوف مؤقتاً",
            AppLanguage.FRENCH to "En Pause"
        ),
        "lock_status" to mapOf(
            AppLanguage.ENGLISH to "Lock Status",
            AppLanguage.ARABIC to "حالة القفل",
            AppLanguage.FRENCH to "Statut de verrouillage"
        ),
        "online" to mapOf(
            AppLanguage.ENGLISH to "Online",
            AppLanguage.ARABIC to "متصل",
            AppLanguage.FRENCH to "En ligne"
        ),
        "offline" to mapOf(
            AppLanguage.ENGLISH to "Offline",
            AppLanguage.ARABIC to "غير متصل",
            AppLanguage.FRENCH to "Hors ligne"
        ),
        "bedtime_lock" to mapOf(
            AppLanguage.ENGLISH to "Bedtime Lockdown",
            AppLanguage.ARABIC to "قفل وقت النوم",
            AppLanguage.FRENCH to "Verrouillage Coucher"
        ),
        "daily_limit" to mapOf(
            AppLanguage.ENGLISH to "Daily Screen Limit",
            AppLanguage.ARABIC to "الحد اليومي للشاشة",
            AppLanguage.FRENCH to "Limite quotidienne"
        ),
        "parent_override_title" to mapOf(
            AppLanguage.ENGLISH to "Parent Bypass Override",
            AppLanguage.ARABIC to "تجاوز وإلغاء القفل للأبوين",
            AppLanguage.FRENCH to "Remplacement du parent"
        ),
        "parent_override_desc" to mapOf(
            AppLanguage.ENGLISH to "Enter the Parental Security PIN to unlock or bypass Haris Shield on this companion device.",
            AppLanguage.ARABIC to "أدخل رمز PIN الأمني للأبوين لفك قفل تطبيق حارس أو تجاوزه على جهاز الطفل.",
            AppLanguage.FRENCH to "Saisissez le code PIN de sécurité parental pour déverrouiller ou contourner le bouclier Haris sur cet appareil."
        ),
        "pin_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Enter parent PIN (default is active)",
            AppLanguage.ARABIC to "أدخل PIN الأبوي (الافتراضي مفعّل)",
            AppLanguage.FRENCH to "Code PIN (par défaut actif)"
        ),
        "wrong_pin" to mapOf(
            AppLanguage.ENGLISH to "Incorrect safety code PIN. Access Denied.",
            AppLanguage.ARABIC to "رمز الأمان PIN غير صحيح. تم رفض الدخول.",
            AppLanguage.FRENCH to "Code PIN incorrect. Accès refusé."
        ),
        "log_out" to mapOf(
            AppLanguage.ENGLISH to "Parent Sign Out",
            AppLanguage.ARABIC to "تسجيل خروج الوالدين",
            AppLanguage.FRENCH to "Déconnexion de Parent"
        ),
        "parent_auth_title" to mapOf(
            AppLanguage.ENGLISH to "Parent Access Shield",
            AppLanguage.ARABIC to "درع وصول الآباء والأمهات",
            AppLanguage.FRENCH to "Bouclier d'accès Parent"
        ),
        "parent_auth_subtitle" to mapOf(
            AppLanguage.ENGLISH to "Firebase Secure Authentication & Cloud Sync Shield",
            AppLanguage.ARABIC to "مصادقة فايربيس الآمنة وتزامن السحابة",
            AppLanguage.FRENCH to "Authentification sécurisée Firebase & Synchro Cloud"
        ),
        "enter_email" to mapOf(
            AppLanguage.ENGLISH to "Parent Email Address",
            AppLanguage.ARABIC to "البريد الإلكتروني للوالد",
            AppLanguage.FRENCH to "Adresse Email du Parent"
        ),
        "enter_password" to mapOf(
            AppLanguage.ENGLISH to "Master Access Password",
            AppLanguage.ARABIC to "كلمة المرور الرئيسية للوصول",
            AppLanguage.FRENCH to "Mot de Passe Principal"
        ),
        "confirm_password" to mapOf(
            AppLanguage.ENGLISH to "Confirm Password",
            AppLanguage.ARABIC to "تأكيد كلمة المرور",
            AppLanguage.FRENCH to "Confirmer le Mot de Passe"
        ),
        "login_now" to mapOf(
            AppLanguage.ENGLISH to "Secure Parent Sign In",
            AppLanguage.ARABIC to "تسجيل الدخول الآمن للوالدين",
            AppLanguage.FRENCH to "Connexion Sécurisée Parent"
        ),
        "signup_now" to mapOf(
            AppLanguage.ENGLISH to "Register Master Account",
            AppLanguage.ARABIC to "تسجيل حساب رئيسي جديد",
            AppLanguage.FRENCH to "Créer un Compte Principal"
        ),
        "no_account_yet" to mapOf(
            AppLanguage.ENGLISH to "No parent administrator account configured? Create one in seconds:",
            AppLanguage.ARABIC to "لم يتم تكوين حساب مدير الوالدين؟ أنشئ حسابًا في ثوانٍ:",
            AppLanguage.FRENCH to "Pas encore de compte administrateur ? Créez-en un en quelques secondes :"
        ),
        "already_have_account" to mapOf(
            AppLanguage.ENGLISH to "Already registered? Click below to sign in:",
            AppLanguage.ARABIC to "مُسجّل بالفعل؟ انقر أدناه لتسجيل الدخول:",
            AppLanguage.FRENCH to "Déjà inscrit ? Cliquez ci-dessous pour vous connecter :"
        ),
        "auth_error_fields" to mapOf(
            AppLanguage.ENGLISH to "Please fill in all email and password fields correctly.",
            AppLanguage.ARABIC to "يرجى ملء كافة حقول البريد الإلكتروني وكلمة المرور بشكل صحيح.",
            AppLanguage.FRENCH to "Veuillez remplir correctement tous les champs."
        ),
        "auth_error_mismatch" to mapOf(
            AppLanguage.ENGLISH to "Verify passwords: the confirm input does not match.",
            AppLanguage.ARABIC to "تحقق من كلمات المرور: تأكيد كلمة المرور غير متطابق.",
            AppLanguage.FRENCH to "Vérifiez les mots de passe : la confirmation ne correspond pas."
        ),
        "auth_error_invalid" to mapOf(
            AppLanguage.ENGLISH to "Invalid password or email registered credentials.",
            AppLanguage.ARABIC to "البريد الإلكتروني أو كلمة المرور غير صالحة.",
            AppLanguage.FRENCH to "Email ou mot de passe invalide."
        ),
        "secured_by_firebase" to mapOf(
            AppLanguage.ENGLISH to "Secured by Firebase Mobile Auth & Offline Local Shield",
            AppLanguage.ARABIC to "محمي بمصادقة فايربيس والدرع المحلي",
            AppLanguage.FRENCH to "Sécurisé par Firebase Mobile Auth & Bouclier Local"
        ),
        "or_continue_with" to mapOf(
            AppLanguage.ENGLISH to "Or continue with",
            AppLanguage.ARABIC to "أو تواصل باستخدام",
            AppLanguage.FRENCH to "Ou continuer avec"
        ),
        "sign_in_google" to mapOf(
            AppLanguage.ENGLISH to "Google / Gmail",
            AppLanguage.ARABIC to "جوجل / جيميل",
            AppLanguage.FRENCH to "Google / Gmail"
        ),
        "sign_in_facebook" to mapOf(
            AppLanguage.ENGLISH to "Facebook",
            AppLanguage.ARABIC to "فيسبوك",
            AppLanguage.FRENCH to "Facebook"
        ),
        "choose_google_account" to mapOf(
            AppLanguage.ENGLISH to "Choose an account",
            AppLanguage.ARABIC to "اختر حسابًا",
            AppLanguage.FRENCH to "Choisir un compte"
        ),
        "continue_to_app" to mapOf(
            AppLanguage.ENGLISH to "to continue to Haris Family Safety",
            AppLanguage.ARABIC to "للمتابعة إلى حارس سلامة الأسرة",
            AppLanguage.FRENCH to "pour continuer vers Haris"
        ),
        "fb_sandbox_title" to mapOf(
            AppLanguage.ENGLISH to "Facebook Sandbox Active",
            AppLanguage.ARABIC to "بيئة فيسبوك التجريبية نشطة",
            AppLanguage.FRENCH to "Facebook Sandbox Actif"
        ),
        "fb_sandbox_desc" to mapOf(
            AppLanguage.ENGLISH to "Facebook Developer Identity Verification is currently pending. While we finish registering our legal business entity, you can simulate Developer Sandbox mode or use a registered administrator account in our development console.",
            AppLanguage.ARABIC to "التحقق من هوية مطور فيسبوك معلق حاليًا. أثناء انتهائنا من تسجيل كياننا التجاري القانوني، يمكنك محاكاة وضع المطور أو استخدام حساب مشرف مسجل.",
            AppLanguage.FRENCH to "La validation d'identité Facebook Developer est en cours. Pendant que nous finalisons notre entité légale, vous pouvez simuler le Sandbox de test ou utiliser un compte administrateur."
        ),
        "facebook_simulate" to mapOf(
            AppLanguage.ENGLISH to "Simulate Sandbox Login",
            AppLanguage.ARABIC to "محاكاة تسجيل دخول المطور",
            AppLanguage.FRENCH to "Simuler Connexion Sandbox"
        ),
        "bypass_text" to mapOf(
            AppLanguage.ENGLISH to "Parent Override",
            AppLanguage.ARABIC to "تجاوز الأبوي",
            AppLanguage.FRENCH to "Contourner Parent"
        ),
        "shield_title" to mapOf(
            AppLanguage.ENGLISH to "Haris Shield Companion",
            AppLanguage.ARABIC to "مرافق درع حارس",
            AppLanguage.FRENCH to "Bouclier Compagnon Haris"
        ),
        "shield_active_desc" to mapOf(
            AppLanguage.ENGLISH to "This device is protected and monitored in real-time.",
            AppLanguage.ARABIC to "هذا الجهاز محمي ويخضع للمراقبة الفورية في الوقت الفعلي.",
            AppLanguage.FRENCH to "Cet appareil est protégé et surveillé en temps réel."
        ),
        "shield_locked_desc" to mapOf(
            AppLanguage.ENGLISH to "Device Lockdown Active. Remotely locked by Parent.",
            AppLanguage.ARABIC to "وضع الإغلاق التام مفعل. مغلق عن بعد بواسطة الأبوين.",
            AppLanguage.FRENCH to "Verrouillage de l'appareil actif. Bloqué à distance par le parent."
        ),
        "permissions_checklist" to mapOf(
            AppLanguage.ENGLISH to "Accessibility & Guard Services Checklist",
            AppLanguage.ARABIC to "قائمة خدمات الحضانة والوصول للحماية",
            AppLanguage.FRENCH to "Liste de contrôle d'accessibilité et de garde"
        ),
        "system_accessibility" to mapOf(
            AppLanguage.ENGLISH to "System Accessibility Service",
            AppLanguage.ARABIC to "خدمة إمكانية الوصول للنظام",
            AppLanguage.FRENCH to "Service d'accessibilité système"
        ),
        "system_accessibility_desc" to mapOf(
            AppLanguage.ENGLISH to "Required to block unwanted apps, monitor search terms and secure device overlays blockages.",
            AppLanguage.ARABIC to "مطلوبة لحظر التطبيقات غير المرغوب فيها، ومراقبة مصطلحات البحث وقفل الشاشات غير المصرح بها.",
            AppLanguage.FRENCH to "Requis pour bloquer les applications indésirables, surveiller les recherches et sécuriser l'appareil."
        ),
        "overlay_permission" to mapOf(
            AppLanguage.ENGLISH to "Overlay Screen Permissions",
            AppLanguage.ARABIC to "صلاحيات الرسم فوق التطبيقات",
            AppLanguage.FRENCH to "Autorisations d'incrustation"
        ),
        "overlay_permission_desc" to mapOf(
            AppLanguage.ENGLISH to "Allows rendering the lock overlay block screen over restricted apps instantly.",
            AppLanguage.ARABIC to "تسمح هذه الصلاحية بعرض شاشة القفل فوق التطبيقات المقيدة فوراً.",
            AppLanguage.FRENCH to "Permet d'afficher l'écran de verrouillage sur les applications restreintes instantanément."
        ),
        "device_admin" to mapOf(
            AppLanguage.ENGLISH to "Device Policy Admin Card",
            AppLanguage.ARABIC to "صلاحية مدير ومسؤول سياسة الجهاز",
            AppLanguage.FRENCH to "Administration des règles de l'appareil"
        ),
        "device_admin_desc" to mapOf(
            AppLanguage.ENGLISH to "Prevents unauthorized uninstallation of the security companion app.",
            AppLanguage.ARABIC to "تمنع إلغاء تثبيت تطبيق المرافق الأمني بدون إذن الأبوين.",
            AppLanguage.FRENCH to "Empêche la désinstallation non autorisée de l'application de sécurité."
        ),
        "gps_tracking" to mapOf(
            AppLanguage.ENGLISH to "GPS Location Tracking",
            AppLanguage.ARABIC to "تتبع موقع GPS الجغرافي",
            AppLanguage.FRENCH to "Suivi de localisation GPS"
        ),
        "gps_tracking_desc" to mapOf(
            AppLanguage.ENGLISH to "Needed to geolocate school check-ins and trigger real-time neighborhood routes simulations.",
            AppLanguage.ARABIC to "ضرورية لتحديد حضور ومغادرة المدرسة وتفعيل عمليات محاكاة مسارات الحي الفعالة.",
            AppLanguage.FRENCH to "Nécessaire pour géolocaliser l'école et simuler les trajets de quartier en temps réel."
        ),
        "pairing_sentinel" to mapOf(
            AppLanguage.ENGLISH to "Link / Register Sentinel",
            AppLanguage.ARABIC to "ربط وتسجيل الحارس",
            AppLanguage.FRENCH to "Associer / Enregistrer Sentinelle"
        ),
        "pairing_sentinel_desc" to mapOf(
            AppLanguage.ENGLISH to "Enter the 6-Digit code displayed in your Parental control dashboard to register this phone:",
            AppLanguage.ARABIC to "أدخل الرمز المكون من 6 أرقام والمعروض في لوحة الآباء لتسجيل هذا الهاتف:",
            AppLanguage.FRENCH to "Entrez le code à 6 chiffres affiché sur votre tableau de bord parental pour enregistrer ce téléphone :"
        ),
        "register_shield" to mapOf(
            AppLanguage.ENGLISH to "Configure Shield Sentinel & Connect",
            AppLanguage.ARABIC to "تكوين درع الحارس والاتصال",
            AppLanguage.FRENCH to "Configurer la Sentinelle & Connecter"
        ),
        "recent_alerts" to mapOf(
            AppLanguage.ENGLISH to "Child's Digital Stream Activity",
            AppLanguage.ARABIC to "دفتر أنشطة الطفل الرقمية",
            AppLanguage.FRENCH to "Flux d'activité numérique de l'enfant"
        ),
        "ai_coach_praise" to mapOf(
            AppLanguage.ENGLISH to "Haris AI Safe Coach Advice",
            AppLanguage.ARABIC to "إرشادات مدرب حارس بالذكاء الاصطناعي",
            AppLanguage.FRENCH to "Conseils de l'assistant IA de Haris"
        ),
        "child_paired_info" to mapOf(
            AppLanguage.ENGLISH to "Child Paired Profile Info",
            AppLanguage.ARABIC to "تفاصيل ملف الطفل المقترن",
            AppLanguage.FRENCH to "Profil de l'enfant jumelé"
        ),
        "age" to mapOf(
            AppLanguage.ENGLISH to "Age",
            AppLanguage.ARABIC to "العمر",
            AppLanguage.FRENCH to "Âge"
        ),
        "device" to mapOf(
            AppLanguage.ENGLISH to "Device Mode",
            AppLanguage.ARABIC to "وضعية الجهاز",
            AppLanguage.FRENCH to "Mode de l'appareil"
        ),
        "battery" to mapOf(
            AppLanguage.ENGLISH to "Battery Status",
            AppLanguage.ARABIC to "حالة البطارية",
            AppLanguage.FRENCH to "Statut de la batterie"
        ),
        "add_child" to mapOf(
            AppLanguage.ENGLISH to "Add Child Device",
            AppLanguage.ARABIC to "إضافة جهاز طفل جديد",
            AppLanguage.FRENCH to "Ajouter un appareil enfant"
        ),
        "child_selector" to mapOf(
            AppLanguage.ENGLISH to "Select Monitored Child",
            AppLanguage.ARABIC to "اختر الطفل المراقب",
            AppLanguage.FRENCH to "Sélectionner l'enfant suivi"
        ),
        "quick_actions" to mapOf(
            AppLanguage.ENGLISH to "Remote Child Controls",
            AppLanguage.ARABIC to "تحكم الطفل عن بعد",
            AppLanguage.FRENCH to "Boutons de contrôle à distance"
        ),
        "force_lock" to mapOf(
            AppLanguage.ENGLISH to "Remote Shield Lockdown",
            AppLanguage.ARABIC to "تفعيل قفل درع حارس عن بعد",
            AppLanguage.FRENCH to "Verrouillage à distance"
        ),
        "add_fifteen" to mapOf(
            AppLanguage.ENGLISH to "+15m PlayTime",
            AppLanguage.ARABIC to "+15 دقيقة وقت لعب لـ",
            AppLanguage.FRENCH to "+15 m Temps"
        ),
        "walk_simulate" to mapOf(
            AppLanguage.ENGLISH to "Simulate Ride",
            AppLanguage.ARABIC to "محاكاة تحرك المسار",
            AppLanguage.FRENCH to "Simuler Trajet"
        ),
        "screen_time_util" to mapOf(
            AppLanguage.ENGLISH to "Child Screen Time Metrics",
            AppLanguage.ARABIC to "مؤشرات وقت الشاشة للطفل",
            AppLanguage.FRENCH to "Métriques d'utilisation écran"
        ),
        "used_of" to mapOf(
            AppLanguage.ENGLISH to "used of",
            AppLanguage.ARABIC to "مستهلكة من أصل",
            AppLanguage.FRENCH to "utilisés sur"
        ),
        "min" to mapOf(
            AppLanguage.ENGLISH to "min",
            AppLanguage.ARABIC to "دقيقة",
            AppLanguage.FRENCH to "min"
        ),
        "bedtime_schedule" to mapOf(
            AppLanguage.ENGLISH to "Bedtime Security Lock Hours",
            AppLanguage.ARABIC to "ساعات حظر وقت النوم المحمي",
            AppLanguage.FRENCH to "Heures de coucher protégées"
        ),
        "blocked_apps" to mapOf(
            AppLanguage.ENGLISH to "Blocked Applications Sentinel",
            AppLanguage.ARABIC to "تطبيقات حظر درع الأطفال المحددة",
            AppLanguage.FRENCH to "Applications bloquées par la Sentinelle"
        ),
        "filtered_web" to mapOf(
            AppLanguage.ENGLISH to "Enforced SafeSearch Categories",
            AppLanguage.ARABIC to "تصنيفات تصفح حماية الويب الإجبارية",
            AppLanguage.FRENCH to "Filtres SafeSearch de navigation"
        ),
        "clear_logs" to mapOf(
            AppLanguage.ENGLISH to "Clean Activities History",
            AppLanguage.ARABIC to "تنظيف سجل عمليات الهاتف",
            AppLanguage.FRENCH to "Effacer l'historique de recherche"
        ),
        "geofence_status" to mapOf(
            AppLanguage.ENGLISH to "Real-Time Tracking Terminal",
            AppLanguage.ARABIC to "جهاز تتبع الموقع اللحظي الجغرافي",
            AppLanguage.FRENCH to "Borne de suivi géographique"
        ),
        "safe_fences" to mapOf(
            AppLanguage.ENGLISH to "Configured Neighborhood Safe Geofences",
            AppLanguage.ARABIC to "سياج الأمان المعد للحي",
            AppLanguage.FRENCH to "Barrières géographiques sécurisées"
        ),
        "home" to mapOf(
            AppLanguage.ENGLISH to "Home Zone (Charing Cross)",
            AppLanguage.ARABIC to "منطقة المنزل (شارع ديدوش مراد)",
            AppLanguage.FRENCH to "Zone Maison (Rue Didouche)"
        ),
        "school" to mapOf(
            AppLanguage.ENGLISH to "School Campus Zone (Lincoln)",
            AppLanguage.ARABIC to "منطقة المدرسة (ثانوية الأمير عبد القادر)",
            AppLanguage.FRENCH to "Zone École (Lycée Émir)"
        ),
        "coach_header" to mapOf(
            AppLanguage.ENGLISH to "Converse with AI Child Safety Coach",
            AppLanguage.ARABIC to "تحاور مع مرشد سلامة العائلات الذكي",
            AppLanguage.FRENCH to "Discutez avec le conseiller IA de sécurité"
        ),
        "analyze" to mapOf(
            AppLanguage.ENGLISH to "Analyze Logs with Gemini",
            AppLanguage.ARABIC to "تحليل السجل باستخدام Gemini",
            AppLanguage.FRENCH to "Analyser avec Gemini"
        ),
        "chat_placeholder" to mapOf(
            AppLanguage.ENGLISH to "Ask about parenting tips or restrict methods...",
            AppLanguage.ARABIC to "اسأل عن نصائح تربوية أو طرق التقييد...",
            AppLanguage.FRENCH to "Posez une question sur l'éducation ou la sécurité..."
        ),
        "send" to mapOf(
            AppLanguage.ENGLISH to "Send",
            AppLanguage.ARABIC to "إرسال",
            AppLanguage.FRENCH to "Envoyer"
        ),
        "clear_coach_messages" to mapOf(
            AppLanguage.ENGLISH to "Clear Coach Chats",
            AppLanguage.ARABIC to "مسح محادثات المرشد",
            AppLanguage.FRENCH to "Effacer le chat IA"
        )
    )

    fun get(key: String, language: AppLanguage): String {
        return translations[key]?.get(language) ?: key
    }
}

fun AppLanguage.translate(key: String): String {
    return Localization.get(key, this)
}

@Composable
fun ParentalViewModel.trans(key: String): String {
    val lang by this.appLanguage.collectAsStateWithLifecycle()
    return lang.translate(key)
}

@Composable
fun AppPreferencesMenu(
    viewModel: ParentalViewModel,
    modifier: Modifier = Modifier,
    iconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.testTag("app_preferences_button")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Preferences Tab Toggle Menu",
                tint = iconColor
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = viewModel.trans("language"),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                },
                onClick = {},
                enabled = false
            )

            AppLanguage.values().forEach { lang ->
                val selected = (lang == language)
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = lang.displayName, fontSize = 14.sp)
                            if (selected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected Indicator",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    onClick = {
                        viewModel.setAppLanguage(lang)
                        expanded = false
                    }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            DropdownMenuItem(
                text = {
                    Text(
                        text = viewModel.trans("theme"),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                },
                onClick = {},
                enabled = false
            )

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Theme Icon Flag",
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isDarkTheme) viewModel.trans("light_mode") else viewModel.trans("dark_mode"),
                            fontSize = 14.sp
                        )
                    }
                },
                onClick = {
                    viewModel.toggleTheme()
                    expanded = false
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out icon",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = viewModel.trans("log_out"),
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                },
                onClick = {
                    viewModel.logoutParent()
                    expanded = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineLanguageAndThemePicker(
    viewModel: ParentalViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Language Selection Selectors
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language Selector icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = viewModel.trans("language"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = lang == language
                        val flag = when (lang) {
                            AppLanguage.ENGLISH -> "🇬🇧"
                            AppLanguage.ARABIC -> "🇩🇿"
                            AppLanguage.FRENCH -> "🇫🇷"
                        }
                        
                        val abbrev = when (lang) {
                            AppLanguage.ENGLISH -> "EN"
                            AppLanguage.ARABIC -> "AR"
                            AppLanguage.FRENCH -> "FR"
                        }
                        
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setAppLanguage(lang) },
                            label = { 
                                Text(
                                    text = "$flag $abbrev",
                                    fontSize = 11.sp, 
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.height(28.dp).testTag("lang_chip_${lang.code}")
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

            // Theme Selection Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Theme Selector icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = viewModel.trans("theme"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Light Mode Switch
                    FilterChip(
                        selected = !isDarkTheme,
                        onClick = { viewModel.setDarkTheme(false) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.LightMode, null, modifier = Modifier.size(12.dp))
                                Text(viewModel.trans("light_mode"), fontSize = 11.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.height(28.dp)
                    )

                    // Dark Mode Switch
                    FilterChip(
                        selected = isDarkTheme,
                        onClick = { viewModel.setDarkTheme(true) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.DarkMode, null, modifier = Modifier.size(12.dp))
                                Text(viewModel.trans("dark_mode"), fontSize = 11.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}

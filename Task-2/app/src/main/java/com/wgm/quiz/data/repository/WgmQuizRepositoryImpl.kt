package com.wgm.quiz.data.repository

import com.wgm.quiz.data.local.WgmQuestionDao
import com.wgm.quiz.data.local.WgmQuestionEntity
import com.wgm.quiz.domain.model.WgmQuestion
import com.wgm.quiz.domain.repository.WgmQuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WgmQuizRepositoryImpl(
    private val dao: WgmQuestionDao
) : WgmQuizRepository {

    override suspend fun getQuestion(difficulty: Int): WgmQuestion? = withContext(Dispatchers.IO) {
        dao.getRandomQuestionByDifficulty(difficulty)?.toDomain()
    }

    override suspend fun getAlternativeQuestion(difficulty: Int, excludeId: Long): WgmQuestion? = withContext(Dispatchers.IO) {
        dao.getAlternativeQuestion(difficulty, excludeId)?.toDomain()
    }

    override suspend fun seedQuestionsIfEmpty() = withContext(Dispatchers.IO) {
        val count = dao.getQuestionCount()
        if (count == 0) {
            android.util.Log.d("WgmQuizRepositoryImpl", "Seeding DB with 45 real questions")
            val questions = mutableListOf<WgmQuestionEntity>()

            // ── LEVEL 1 — Easy: Pop Culture & Everyday Life ──────────────────────────
            questions.add(WgmQuestionEntity(
                text = "Which social media platform is known for its 280-character text posts?",
                optionA = "Instagram", optionB = "Twitter / X", optionC = "Snapchat", optionD = "Pinterest",
                correctAnswerIndex = 1, difficulty = 1
            ))
            questions.add(WgmQuestionEntity(
                text = "What colour is the sky on a clear day?",
                optionA = "Green", optionB = "Yellow", optionC = "Blue", optionD = "Orange",
                correctAnswerIndex = 2, difficulty = 1
            ))
            questions.add(WgmQuestionEntity(
                text = "How many days are there in a leap year?",
                optionA = "365", optionB = "366", optionC = "364", optionD = "367",
                correctAnswerIndex = 1, difficulty = 1
            ))

            // ── LEVEL 2 — Easy: General Knowledge ────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "Which animal is known as the 'King of the Jungle'?",
                optionA = "Tiger", optionB = "Leopard", optionC = "Cheetah", optionD = "Lion",
                correctAnswerIndex = 3, difficulty = 2
            ))
            questions.add(WgmQuestionEntity(
                text = "How many sides does a hexagon have?",
                optionA = "5", optionB = "7", optionC = "6", optionD = "8",
                correctAnswerIndex = 2, difficulty = 2
            ))
            questions.add(WgmQuestionEntity(
                text = "What is the chemical symbol for water?",
                optionA = "HO", optionB = "H2O", optionC = "O2H", optionD = "WTR",
                correctAnswerIndex = 1, difficulty = 2
            ))

            // ── LEVEL 3 — Easy: India Basics ────────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "What is the capital city of India?",
                optionA = "Mumbai", optionB = "Kolkata", optionC = "Chennai", optionD = "New Delhi",
                correctAnswerIndex = 3, difficulty = 3
            ))
            questions.add(WgmQuestionEntity(
                text = "How many states are there in India as of 2024?",
                optionA = "29", optionB = "30", optionC = "28", optionD = "27",
                correctAnswerIndex = 2, difficulty = 3
            ))
            questions.add(WgmQuestionEntity(
                text = "Which river is the longest in India?",
                optionA = "Brahmaputra", optionB = "Krishna", optionC = "Yamuna", optionD = "Ganga",
                correctAnswerIndex = 3, difficulty = 3
            ))

            // ── LEVEL 4 — Medium-Easy: Sports ────────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "How many players are on the field for one team in a standard football (soccer) match?",
                optionA = "10", optionB = "12", optionC = "11", optionD = "9",
                correctAnswerIndex = 2, difficulty = 4
            ))
            questions.add(WgmQuestionEntity(
                text = "In cricket, how many balls are in one standard over?",
                optionA = "5", optionB = "8", optionC = "4", optionD = "6",
                correctAnswerIndex = 3, difficulty = 4
            ))
            questions.add(WgmQuestionEntity(
                text = "Which country has won the most FIFA World Cup titles?",
                optionA = "Germany", optionB = "Argentina", optionC = "Brazil", optionD = "Italy",
                correctAnswerIndex = 2, difficulty = 4
            ))

            // ── LEVEL 5 — Medium-Easy: Geography ─────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "Which is the largest continent by area?",
                optionA = "Africa", optionB = "North America", optionC = "Australia", optionD = "Asia",
                correctAnswerIndex = 3, difficulty = 5
            ))
            questions.add(WgmQuestionEntity(
                text = "What is the longest river in the world?",
                optionA = "Amazon", optionB = "Yangtze", optionC = "Mississippi", optionD = "Nile",
                correctAnswerIndex = 3, difficulty = 5
            ))
            questions.add(WgmQuestionEntity(
                text = "Which country has the largest population in the world as of 2024?",
                optionA = "USA", optionB = "India", optionC = "China", optionD = "Indonesia",
                correctAnswerIndex = 1, difficulty = 5
            ))

            // ── LEVEL 6 — Medium-Easy: History ───────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "In which year did India gain independence from British rule?",
                optionA = "1948", optionB = "1950", optionC = "1945", optionD = "1947",
                correctAnswerIndex = 3, difficulty = 6
            ))
            questions.add(WgmQuestionEntity(
                text = "Who was the first President of the United States?",
                optionA = "Abraham Lincoln", optionB = "George Washington", optionC = "John Adams", optionD = "Thomas Jefferson",
                correctAnswerIndex = 1, difficulty = 6
            ))
            questions.add(WgmQuestionEntity(
                text = "Which ancient wonder of the world still stands today?",
                optionA = "Hanging Gardens of Babylon", optionB = "Colossus of Rhodes", optionC = "Great Pyramid of Giza", optionD = "Lighthouse of Alexandria",
                correctAnswerIndex = 2, difficulty = 6
            ))

            // ── LEVEL 7 — Medium: Science & Technology ───────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "What does 'CPU' stand for in computing?",
                optionA = "Central Program Utility", optionB = "Core Processing Unit", optionC = "Central Processing Unit", optionD = "Computer Processing Utility",
                correctAnswerIndex = 2, difficulty = 7
            ))
            questions.add(WgmQuestionEntity(
                text = "What is the speed of light in a vacuum (approximately)?",
                optionA = "3 × 10⁸ m/s", optionB = "3 × 10⁶ m/s", optionC = "3 × 10⁷ m/s", optionD = "3 × 10⁵ m/s",
                correctAnswerIndex = 0, difficulty = 7
            ))
            questions.add(WgmQuestionEntity(
                text = "Which company developed the Android operating system?",
                optionA = "Apple", optionB = "Microsoft", optionC = "Samsung", optionD = "Google",
                correctAnswerIndex = 3, difficulty = 7
            ))

            // ── LEVEL 8 — Medium: Indian Culture & Awards ────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "Which Indian film won the Academy Award for Best Picture in 2009 (for a film released in 2008)?",
                optionA = "Lagaan", optionB = "Mother India", optionC = "Slumdog Millionaire", optionD = "RRR",
                correctAnswerIndex = 2, difficulty = 8
            ))
            questions.add(WgmQuestionEntity(
                text = "Who wrote the Indian National Anthem 'Jana Gana Mana'?",
                optionA = "Bankim Chandra Chattopadhyay", optionB = "Rabindranath Tagore", optionC = "Sarojini Naidu", optionD = "Subramanya Bharati",
                correctAnswerIndex = 1, difficulty = 8
            ))
            questions.add(WgmQuestionEntity(
                text = "Which Indian city is known as the 'Silicon Valley of India'?",
                optionA = "Hyderabad", optionB = "Pune", optionC = "Chennai", optionD = "Bengaluru",
                correctAnswerIndex = 3, difficulty = 8
            ))

            // ── LEVEL 9 — Medium: Space & Physics ───────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "Which planet in our solar system has the most moons?",
                optionA = "Jupiter", optionB = "Uranus", optionC = "Saturn", optionD = "Neptune",
                correctAnswerIndex = 2, difficulty = 9
            ))
            questions.add(WgmQuestionEntity(
                text = "What is the name of the force that keeps planets in orbit around the Sun?",
                optionA = "Electromagnetic force", optionB = "Nuclear force", optionC = "Gravity", optionD = "Centripetal force",
                correctAnswerIndex = 2, difficulty = 9
            ))
            questions.add(WgmQuestionEntity(
                text = "Who proposed the Special Theory of Relativity?",
                optionA = "Isaac Newton", optionB = "Niels Bohr", optionC = "Max Planck", optionD = "Albert Einstein",
                correctAnswerIndex = 3, difficulty = 9
            ))

            // ── LEVEL 10 — Hard: World History ───────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "The Treaty of Versailles in 1919 officially ended which global conflict?",
                optionA = "The Crimean War", optionB = "World War II", optionC = "The Cold War", optionD = "World War I",
                correctAnswerIndex = 3, difficulty = 10
            ))
            questions.add(WgmQuestionEntity(
                text = "Who was the first woman to win a Nobel Prize?",
                optionA = "Rosalind Franklin", optionB = "Malala Yousafzai", optionC = "Marie Curie", optionD = "Mother Teresa",
                correctAnswerIndex = 2, difficulty = 10
            ))
            questions.add(WgmQuestionEntity(
                text = "The ancient city of Carthage was located in which modern-day country?",
                optionA = "Morocco", optionB = "Libya", optionC = "Algeria", optionD = "Tunisia",
                correctAnswerIndex = 3, difficulty = 10
            ))

            // ── LEVEL 11 — Hard: Advanced Science ────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "What is the atomic number of Gold (Au)?",
                optionA = "79", optionB = "82", optionC = "47", optionD = "78",
                correctAnswerIndex = 0, difficulty = 11
            ))
            questions.add(WgmQuestionEntity(
                text = "Which law of thermodynamics states that energy cannot be created or destroyed?",
                optionA = "Zeroth Law", optionB = "Second Law", optionC = "Third Law", optionD = "First Law",
                correctAnswerIndex = 3, difficulty = 11
            ))
            questions.add(WgmQuestionEntity(
                text = "The phenomenon of 'Quantum Entanglement' was famously described by Einstein as what?",
                optionA = "Spooky action at a distance", optionB = "Cosmic synchronicity", optionC = "Parallel resonance", optionD = "Wave-particle duality",
                correctAnswerIndex = 0, difficulty = 11
            ))

            // ── LEVEL 12 — Hard: Mathematics & Logic ──────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "What is the value of 'e' (Euler's number) to two decimal places?",
                optionA = "3.14", optionB = "2.71", optionC = "1.61", optionD = "2.56",
                correctAnswerIndex = 1, difficulty = 12
            ))
            questions.add(WgmQuestionEntity(
                text = "If a prime number p > 2, which of the following is always true about p?",
                optionA = "p is divisible by 3", optionB = "p is even", optionC = "p is odd", optionD = "p ends in 1 or 7",
                correctAnswerIndex = 2, difficulty = 12
            ))
            questions.add(WgmQuestionEntity(
                text = "What is the sum of interior angles of a polygon with 10 sides?",
                optionA = "1260°", optionB = "1620°", optionC = "1080°", optionD = "1440°",
                correctAnswerIndex = 3, difficulty = 12
            ))

            // ── LEVEL 13 — Expert: Deep Indian History ─────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "The battle of Plassey (1757) was fought between the British East India Company and which Nawab?",
                optionA = "Nawab Wajid Ali Shah", optionB = "Nawab Siraj ud-Daulah", optionC = "Nawab Tipu Sultan", optionD = "Nawab Hyder Ali",
                correctAnswerIndex = 1, difficulty = 13
            ))
            questions.add(WgmQuestionEntity(
                text = "Which Mauryan emperor issued the famous 'Edict of Kalinga' after witnessing the devastation of war?",
                optionA = "Chandragupta Maurya", optionB = "Bindusara", optionC = "Ashoka the Great", optionD = "Dasaratha Maurya",
                correctAnswerIndex = 2, difficulty = 13
            ))
            questions.add(WgmQuestionEntity(
                text = "The 'Drain of Wealth' theory — arguing that India's wealth was systematically transferred to Britain — was formulated by which Indian economist?",
                optionA = "Bal Gangadhar Tilak", optionB = "Gopal Krishna Gokhale", optionC = "Dadabhai Naoroji", optionD = "Mahadev Govind Ranade",
                correctAnswerIndex = 2, difficulty = 13
            ))

            // ── LEVEL 14 — Expert: Advanced Tech & Science ────────────────────────
            questions.add(WgmQuestionEntity(
                text = "In computer science, what does 'O(n log n)' represent in the context of sorting algorithms?",
                optionA = "Constant time complexity", optionB = "Quadratic time complexity", optionC = "Linearithmic time complexity", optionD = "Exponential time complexity",
                correctAnswerIndex = 2, difficulty = 14
            ))
            questions.add(WgmQuestionEntity(
                text = "The Chandrasekhar Limit (approximately 1.4 solar masses) defines the maximum mass of what type of stellar remnant?",
                optionA = "Neutron Star", optionB = "White Dwarf", optionC = "Black Hole", optionD = "Red Giant",
                correctAnswerIndex = 1, difficulty = 14
            ))
            questions.add(WgmQuestionEntity(
                text = "Which cryptographic concept does HTTPS primarily rely on to secure web communications?",
                optionA = "Caesar Cipher", optionB = "Symmetric-key encryption only", optionC = "MD5 hashing", optionD = "Asymmetric (Public-Key) Cryptography",
                correctAnswerIndex = 3, difficulty = 14
            ))

            // ── LEVEL 15 — Expert: Grand Finale ──────────────────────────────────────
            questions.add(WgmQuestionEntity(
                text = "Which Indian mathematician independently developed results in areas such as infinite series and modular forms, leaving behind notebooks described as 'containing theorems from the future'?",
                optionA = "Aryabhata", optionB = "Srinivasa Ramanujan", optionC = "Brahmagupta", optionD = "Shakuntala Devi",
                correctAnswerIndex = 1, difficulty = 15
            ))
            questions.add(WgmQuestionEntity(
                text = "The Riemann Hypothesis, one of the Millennium Prize Problems, makes a conjecture about the non-trivial zeros of which mathematical function?",
                optionA = "Gamma function", optionB = "Euler's totient function", optionC = "Riemann Zeta function", optionD = "Bessel function",
                correctAnswerIndex = 2, difficulty = 15
            ))
            questions.add(WgmQuestionEntity(
                text = "In quantum computing, a qubit can represent 0, 1, or both simultaneously. What is this property called?",
                optionA = "Quantum Entanglement", optionB = "Decoherence", optionC = "Quantum Tunnelling", optionD = "Superposition",
                correctAnswerIndex = 3, difficulty = 15
            ))

            android.util.Log.d("WgmQuizRepositoryImpl", "Inserting ${questions.size} real questions into DB")
            dao.insertQuestions(questions)
            android.util.Log.d("WgmQuizRepositoryImpl", "Question seeding complete")
        }
    }

    private fun WgmQuestionEntity.toDomain(): WgmQuestion {
        return WgmQuestion(
            id = id,
            text = text,
            options = listOf(optionA, optionB, optionC, optionD),
            correctAnswerIndex = correctAnswerIndex,
            difficulty = difficulty
        )
    }
}

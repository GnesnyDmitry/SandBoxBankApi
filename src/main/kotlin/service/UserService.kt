package org.example.service

import org.example.generateToken
import org.example.models.BaseProduct
import org.example.models.Card
import org.example.models.Product
import org.example.models.TransactionRequest
import org.example.models.User

sealed class RegisterResult {
    data class Success(val accessToken: String, val refreshToken: String, val userId: Long) : RegisterResult()
    object AlreadyExists : RegisterResult()
    object InvalidEmail : RegisterResult()
    object PasswordTooShort : RegisterResult()
}

sealed class RefreshTokenResult {
    data class Success(val accessToken: String, val refreshToken: String) : RefreshTokenResult()
    object NoExist : RefreshTokenResult()
}

sealed class DebitCardResult {
    data class Success(val card: Card, val requestNumber: Long, val currentCardNumber: Long) :
        DebitCardResult()

    object InCorrectCardNumber : DebitCardResult()
    object InCorrectAccessToken : DebitCardResult()
    object IsCardExist : DebitCardResult()
}

sealed class GetAllCardsResult {
    data class Success(val cards: List<Card>) : GetAllCardsResult()
    object InCorrectAccessToken : GetAllCardsResult()
}

sealed class CreditCardResult {
    data class Success(val card: Card, val requestNumber: Long, val currentCardNumber: Long) :
        CreditCardResult()

    object InCorrectCardNumber : CreditCardResult()
    object InCorrectAccessToken : CreditCardResult()
    object IsCardExist : CreditCardResult()
}

sealed interface GetProductsResult {
    data class Success(val products: List<Product>) : GetProductsResult
    object InCorrectAccessToken : GetProductsResult
}

sealed class SkyTopUpResult {
    object InvalidAccessToken : SkyTopUpResult()
    object TransactionNumberTooLow : SkyTopUpResult()
    object TransactionAlreadyExists : SkyTopUpResult()
    data class Success(val transactionNumber: Long) : SkyTopUpResult()
}

sealed class DepositResult {
    data class Success(
        val product: Product,
        val requestNumber: Long,
        val currentDepositNumber: Long
    ) : DepositResult()

    object InCorrectAccessToken : DepositResult()
    object InCorrectDepositNumber : DepositResult()
    object IsDepositExist : DepositResult()
}

sealed interface TransactionResult {
    data class Success(val transactionNumber: Long) : TransactionResult
    object InvalidToken : TransactionResult
    object InsufficientFunds : TransactionResult
    object InvalidTransactionNumber : TransactionResult
    object AlreadyProcessed : TransactionResult
    object ProductNotFound : TransactionResult
}

sealed class CreateCreditResult {
    data class Success(val product: Product, val requestNumber: Long, val currentCreditNumber: Long) : CreateCreditResult()
    object InCorrectAccessToken : CreateCreditResult()
    object InCorrectCreditNumber : CreateCreditResult()
    object InCorrectBalance : CreateCreditResult()
    object IsCreditExist : CreateCreditResult()
}



class UserService {
    private val users = mutableListOf<User>()
    private val debitCardsByUser = mutableMapOf<String, MutableList<Card>>()
    private val creditCardsByUser = mutableMapOf<String, MutableList<Card>>()
    private val depositsByUser = mutableMapOf<String, MutableList<Product>>()
    private val creditsByUser = mutableMapOf<String, MutableList<Product>>()
    val transactionsByUser = mutableMapOf<String, MutableList<Long>>()
    private var productIdCounter = 1L
    private var userIdCounter = 1L

    fun registerUser(email: String, password: String): RegisterResult {
        if (!isEmailValid(email)) return RegisterResult.InvalidEmail
        if (!isPasswordValid(password)) return RegisterResult.PasswordTooShort
        if (users.any() { it.email == email }) return RegisterResult.AlreadyExists

        val userId = userIdCounter++
        val accessToken = generateToken()
        val refreshToken = generateToken()

        val user = User(
            email = email,
            password = password,
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId
        )

        users.add(user)

        return RegisterResult.Success(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId
        )
    }

    fun authenticateUser(email: String, password: String): RegisterResult {
        if (!isEmailValid(email)) return RegisterResult.InvalidEmail
        if (!isPasswordValid(password)) return RegisterResult.PasswordTooShort
        val user = users.find { it.email == email && it.password == password }
        if (user == null) return RegisterResult.InvalidEmail

        return RegisterResult.Success(
            accessToken = user.accessToken,
            refreshToken = user.refreshToken,
            userId = user.userId
        )
    }

    fun refreshToken(email: String, refreshToken: String): RefreshTokenResult {
        if (!isEmailValid(email)) return RefreshTokenResult.NoExist
        val user = users.find { it.email == email && it.refreshToken == refreshToken }
        if (user == null) return RefreshTokenResult.NoExist

        val newaAccessToken = generateToken()
        user.accessToken = newaAccessToken

        return RefreshTokenResult.Success(
            accessToken = newaAccessToken,
            refreshToken = user.refreshToken
        )
    }

    fun createDebitCard(
        userId: Long,
        accessToken: String,
        currentCardNumber: Long,
        requestNumber: Long
    ): DebitCardResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
        if (user == null) return DebitCardResult.InCorrectAccessToken
        if (currentCardNumber > 5) {
            return DebitCardResult.InCorrectCardNumber
        }

        val userDebitCards = debitCardsByUser.getOrPut(user.email) { mutableListOf() }

        if (userDebitCards.size >= currentCardNumber) {
            return DebitCardResult.IsCardExist
        }

        val newDebitCard = Card(
            id = (2200_0000_0000_0000..2299_9999_9999_9999).random().toLong(),
            cvv = (100..999).random(),
            endDate = "2028-02-02",
            owner = "Ivan Ivanov",
            type = "card_debit",
            percent = 0.0,
            balance = 0
        )

        userDebitCards.add(newDebitCard)

        return DebitCardResult.Success(
            card = newDebitCard,
            requestNumber = requestNumber,
            currentCardNumber = currentCardNumber
        )
    }

    fun createCreditCard(
        userId: Long,
        accessToken: String,
        currentCardNumber: Long,
        requestNumber: Long
    ): CreditCardResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
        if (user == null) return CreditCardResult.InCorrectAccessToken
        if (currentCardNumber > 3) return CreditCardResult.InCorrectCardNumber

        val userCreditCards = creditCardsByUser.getOrPut(user.email) { mutableListOf() }

        if (userCreditCards.size >= currentCardNumber) {
            return CreditCardResult.IsCardExist
        }

        val newCard = Card(
            id = (2200_0000_0000_0000..2299_9999_9999_9999).random().toLong(),
            cvv = (100..999).random(),
            endDate = "2028-02-02",
            owner = "Ivan Ivanov",
            type = "card_credit",
            percent = 33.9,
            balance = 38_000_000
        )

        userCreditCards.add(newCard)

        return CreditCardResult.Success(
            card = newCard,
            requestNumber = requestNumber,
            currentCardNumber = currentCardNumber
        )
    }

    fun getAllCards(userId: Long, accessToken: String): GetAllCardsResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
            ?: return GetAllCardsResult.InCorrectAccessToken

        val debitCards = debitCardsByUser[user.email] ?: emptyList()
        val creditCards = creditCardsByUser[user.email] ?: emptyList()

        return GetAllCardsResult.Success(cards = debitCards + creditCards)
    }

    fun createDeposit(
        userId: Long,
        accessToken: String,
        currentDepositNumber: Long,
        requestNumber: Long,
        percentType: Long,
        period: Long
    ): DepositResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
        if (user == null) return DepositResult.InCorrectAccessToken
        if (currentDepositNumber > 5) return DepositResult.InCorrectDepositNumber

        val userDeposits = depositsByUser.getOrPut(user.email) { mutableListOf() }
        if (userDeposits.size >= currentDepositNumber) {
            return DepositResult.IsDepositExist
        }

        val product = Product(
            id = System.currentTimeMillis(),
            type = "product_deposit",
            percentType = percentType,
            period = period,
            percent = 13,
            balance = 0
        )

        userDeposits.add(product)

        return DepositResult.Success(
            product = product,
            requestNumber = requestNumber,
            currentDepositNumber = currentDepositNumber
        )
    }

    fun createCredit(
        userId: Long,
        accessToken: String,
        currentCreditNumber: Long,
        requestNumber: Long,
        balance: Long,
        period: Long
    ): CreateCreditResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
            ?: return CreateCreditResult.InCorrectAccessToken

        if (currentCreditNumber > 3) return CreateCreditResult.InCorrectCreditNumber
        if (balance <= 0) return CreateCreditResult.InCorrectBalance

        val credits = creditsByUser.getOrPut(user.email) { mutableListOf() }
        if (credits.size >= currentCreditNumber) return CreateCreditResult.IsCreditExist

        val product = Product(
            id = System.currentTimeMillis(),
            type = "product_credit",
            period = period,
            percent = 22,
            balance = balance,
            percentType = 2
        )

        credits.add(product)

        return CreateCreditResult.Success(
            product = product,
            requestNumber = requestNumber,
            currentCreditNumber = currentCreditNumber
        )
    }

    fun getAllProducts(userId: Long, accessToken: String): GetProductsResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
            ?: return GetProductsResult.InCorrectAccessToken

        val deposits = depositsByUser[user.email] ?: emptyList()
        val credits = creditsByUser[user.email] ?: emptyList()

        return GetProductsResult.Success(products = deposits + credits)
    }

    fun makeTransaction(
        userId: Long,
        accessToken: String,
        request: TransactionRequest
    ): TransactionResult {
        val user = users.find { it.userId == userId && it.accessToken == accessToken }
            ?: return TransactionResult.InvalidToken

        val email = user.email

        val previousTransactions = transactionsByUser.getOrPut(email) { mutableListOf() }

        if (previousTransactions.contains(request.transactionNumber)) {
            return TransactionResult.InvalidTransactionNumber
        }

        if (previousTransactions.any { it >= request.transactionNumber }) {
            return TransactionResult.InvalidTransactionNumber
        }

        val from = findProduct(email, request.fromType, request.fromId)
        val to = findProduct(email, request.toType, request.toId)

        if (from == null || to == null || from.balance < request.value) {
            return TransactionResult.ProductNotFound
        }

        if (to.type == "product_credit" && to.balance <= 0) {
            return TransactionResult.InsufficientFunds
        }

        from.balance -= request.value
        to.balance += request.value
        previousTransactions.add(request.transactionNumber)

        return TransactionResult.Success(request.transactionNumber)
    }

    fun findProduct(email: String, type: String, id: Long): BaseProduct? {
        val allProducts: List<BaseProduct> = (depositsByUser[email] ?: emptyList()) +
                (creditsByUser[email] ?: emptyList()) +
                (debitCardsByUser[email] ?: emptyList()) +
                (creditCardsByUser[email] ?: emptyList())
        return allProducts.find { it.id == id && it.type == type }
    }

    fun skyTopUp(
        toId: Long,
        toType: String,
        value: Long,
        transactionNumber: Long,
        accessToken: String
    ): SkyTopUpResult {
        val user = users.find { it.accessToken == accessToken } ?: return SkyTopUpResult.InvalidAccessToken
        val email = user.email

        val userTransactions = transactionsByUser.getOrPut(email) { mutableListOf() }

        if (userTransactions.any { it >= transactionNumber }) {
            return if (userTransactions.contains(transactionNumber)) {
                SkyTopUpResult.TransactionAlreadyExists
            } else {
                SkyTopUpResult.TransactionNumberTooLow
            }
        }
        val to = findProduct(email, toType, toId)
            ?: return SkyTopUpResult.TransactionNumberTooLow

        to.balance += value

        userTransactions.add(transactionNumber)

        return SkyTopUpResult.Success(transactionNumber)
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()
        return email.matches(emailRegex)
    }

    private fun isPasswordValid(password: String): Boolean = password.length >= 7
}
package com.namii.app.features.seller.products.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.client.products.domain.entities.ProductCategory
import com.namii.app.features.seller.products.domain.entities.SellerProductFormInput
import com.namii.app.features.seller.products.domain.entities.SellerProductValidators
import com.namii.app.features.seller.products.domain.usecases.DeleteSellerProductUseCase
import com.namii.app.features.seller.products.domain.usecases.GetSellerProductByIdUseCase
import com.namii.app.features.seller.products.domain.usecases.SaveSellerProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class SellerProductFormUiState(
    val establishmentId: Int = 0,
    val editingProductId: Int? = null,
    val name: String = "",
    val category: ProductCategory = ProductCategory.ALIMENTOS,
    val description: String = "",
    val priceText: String = "",
    val isAvailable: Boolean = true,
    val localPhotoPath: String? = null,
    val existingPhotoUrl: String? = null,
    val nameError: String? = null,
    val descriptionError: String? = null,
    val priceError: String? = null,
    val photoError: String? = null,
    val generalError: String? = null,
    val isLoadingInitial: Boolean = false,
    val isSubmitting: Boolean = false,
    val showDeleteConfirm: Boolean = false,
    val isDeleting: Boolean = false
) {
    val isEditMode: Boolean get() = editingProductId != null
}

@HiltViewModel
class SellerProductFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getProductByIdUseCase: GetSellerProductByIdUseCase,
    private val saveProductUseCase: SaveSellerProductUseCase,
    private val deleteProductUseCase: DeleteSellerProductUseCase
) : ViewModel() {

    private val establishmentId: Int = checkNotNull(savedStateHandle.get<Int>("establishmentId"))
    private val rawProductId: Int = savedStateHandle.get<Int>("productId") ?: -1
    private val editingProductId: Int? = rawProductId.takeIf { it > 0 }

    private val _uiState = MutableStateFlow(
        SellerProductFormUiState(
            establishmentId = establishmentId,
            editingProductId = editingProductId,
            isLoadingInitial = editingProductId != null
        )
    )
    val uiState: StateFlow<SellerProductFormUiState> = _uiState.asStateFlow()

    init {
        if (editingProductId != null) {
            loadExistingProduct(editingProductId)
        }
    }

    fun reloadExistingProduct() {
        val id = editingProductId ?: return
        loadExistingProduct(id)
    }

    private fun loadExistingProduct(productId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingInitial = true, generalError = null) }
            getProductByIdUseCase(productId).fold(
                onSuccess = { product ->
                    _uiState.update {
                        it.copy(
                            name = product.name,
                            category = product.category,
                            description = product.description.orEmpty(),
                            priceText = String.format(Locale.US, "%.2f", product.price),
                            isAvailable = product.isAvailable,
                            existingPhotoUrl = product.photoUrl,
                            isLoadingInitial = false
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            generalError = err.message ?: "No se pudo cargar el producto"
                        )
                    }
                }
            )
        }
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value, nameError = null, generalError = null) }
    }

    fun onCategorySelected(category: ProductCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value, descriptionError = null) }
    }

    fun onPriceChanged(value: String) {
        _uiState.update { it.copy(priceText = value, priceError = null, generalError = null) }
    }

    fun onAvailabilityChanged(isAvailable: Boolean) {
        _uiState.update { it.copy(isAvailable = isAvailable) }
    }

    fun onPhotoConfirmed(localPath: String) {
        _uiState.update { it.copy(localPhotoPath = localPath, photoError = null) }
    }

    fun saveProduct(onSaved: () -> Unit) {
        val state = _uiState.value
        val nameErr = SellerProductValidators.validateName(state.name)
        val descErr = SellerProductValidators.validateDescription(state.description)
        val priceErr = SellerProductValidators.validatePrice(state.priceText)
        val photoErr = SellerProductValidators.validatePhoto(
            localPhotoPath = state.localPhotoPath,
            existingPhotoUrl = state.existingPhotoUrl,
            isEditing = state.isEditMode
        )

        if (nameErr != null || descErr != null || priceErr != null || photoErr != null) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    descriptionError = descErr,
                    priceError = priceErr,
                    photoError = photoErr
                )
            }
            return
        }

        val parsedPrice = state.priceText.trim().replace(",", ".").toDouble()

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, generalError = null) }
            saveProductUseCase(
                establishmentId = establishmentId,
                productId = state.editingProductId,
                input = SellerProductFormInput(
                    name = state.name,
                    category = state.category,
                    description = state.description,
                    price = parsedPrice,
                    isAvailable = state.isAvailable,
                    localPhotoPath = state.localPhotoPath,
                    existingPhotoUrl = state.existingPhotoUrl
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false) }
                    onSaved()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            generalError = err.message ?: "No se pudo guardar el producto"
                        )
                    }
                }
            )
        }
    }

    fun requestDelete() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun dismissDelete() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun confirmDelete(onDeleted: () -> Unit, onError: (String) -> Unit) {
        val targetId = editingProductId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            deleteProductUseCase(targetId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isDeleting = false, showDeleteConfirm = false) }
                    onDeleted()
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isDeleting = false, showDeleteConfirm = false) }
                    onError(err.message ?: "No se pudo eliminar el producto")
                }
            )
        }
    }
}

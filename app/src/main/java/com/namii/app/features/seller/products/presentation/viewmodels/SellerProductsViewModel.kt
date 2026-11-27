package com.namii.app.features.seller.products.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namii.app.features.client.products.domain.entities.Product
import com.namii.app.features.client.products.domain.entities.ProductCategory
import com.namii.app.features.seller.products.domain.usecases.DeleteSellerProductUseCase
import com.namii.app.features.seller.products.domain.usecases.GetMyProductsUseCase
import com.namii.app.features.seller.products.domain.usecases.ToggleSellerProductAvailabilityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellerProductsUiState(
    val establishmentId: Int = 0,
    val allProducts: List<Product> = emptyList(),
    val selectedCategory: ProductCategory? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val pendingDeleteProduct: Product? = null,
    val isDeleting: Boolean = false
) {
    val visibleProducts: List<Product>
        get() = selectedCategory?.let { cat ->
            allProducts.filter { it.category == cat }
        } ?: allProducts
}

@HiltViewModel
class SellerProductsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMyProductsUseCase: GetMyProductsUseCase,
    private val toggleAvailabilityUseCase: ToggleSellerProductAvailabilityUseCase,
    private val deleteSellerProductUseCase: DeleteSellerProductUseCase
) : ViewModel() {

    private val establishmentId: Int = checkNotNull(savedStateHandle.get<Int>("establishmentId"))

    private val _uiState = MutableStateFlow(SellerProductsUiState(establishmentId = establishmentId))
    val uiState: StateFlow<SellerProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getMyProductsUseCase(establishmentId).fold(
                onSuccess = { list ->
                    _uiState.update {
                        it.copy(allProducts = list, isLoading = false, errorMessage = null)
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "No se pudo cargar tu menú"
                        )
                    }
                }
            )
        }
    }

    fun selectCategory(category: ProductCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun toggleAvailability(product: Product, onError: (String) -> Unit) {
        val nextValue = !product.isAvailable
        viewModelScope.launch {
            toggleAvailabilityUseCase(product.id, nextValue).fold(
                onSuccess = { updated ->
                    _uiState.update { state ->
                        state.copy(
                            allProducts = state.allProducts.map {
                                if (it.id == updated.id) updated else it
                            }
                        )
                    }
                },
                onFailure = { err ->
                    onError(err.message ?: "No se pudo cambiar la disponibilidad")
                }
            )
        }
    }

    fun requestDelete(product: Product) {
        _uiState.update { it.copy(pendingDeleteProduct = product) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(pendingDeleteProduct = null) }
    }

    fun confirmDelete(onMessage: (String) -> Unit) {
        val target = _uiState.value.pendingDeleteProduct ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            deleteSellerProductUseCase(target.id).fold(
                onSuccess = {
                    _uiState.update { state ->
                        state.copy(
                            allProducts = state.allProducts.filterNot { it.id == target.id },
                            pendingDeleteProduct = null,
                            isDeleting = false
                        )
                    }
                    onMessage("${target.name} eliminado del menú")
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isDeleting = false, pendingDeleteProduct = null) }
                    onMessage(err.message ?: "No se pudo eliminar el producto")
                }
            )
        }
    }
}

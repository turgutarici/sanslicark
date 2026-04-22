package com.cukurova.cark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cukurova.cark.databinding.FragmentPlayBinding
import kotlin.random.Random

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrizeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.prizes.observe(viewLifecycleOwner) { prizes ->
            binding.wheelView.setPrizes(prizes.map { it.name })
        }

        binding.btnSpin.setOnClickListener {
            val prizes = viewModel.prizes.value ?: emptyList()
            if (prizes.size < 2) {
                Toast.makeText(context, R.string.min_options_warning, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnSpin.isEnabled = false
            val randomAngle = Random.nextFloat() * 360f
            binding.wheelView.spin(randomAngle) { index ->
                binding.btnSpin.isEnabled = true
                val result = prizes[index].name
                binding.tvResult.text = result
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
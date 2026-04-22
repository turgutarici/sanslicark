package com.cukurova.cark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.cukurova.cark.databinding.FragmentOptionsBinding

class OptionsFragment : Fragment() {

    private var _binding: FragmentOptionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrizeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PrizeAdapter(emptyList()) { prize ->
            viewModel.removePrize(prize)
        }

        binding.rvPrizes.layoutManager = LinearLayoutManager(context)
        binding.rvPrizes.adapter = adapter

        binding.btnAdd.setOnClickListener {
            val name = binding.etPrize.text.toString()
            if (name.isNotBlank()) {
                viewModel.addPrize(name)
                binding.etPrize.text?.clear()
            }
        }

        viewModel.prizes.observe(viewLifecycleOwner) { prizes ->
            adapter.updateData(prizes)
            binding.tvCount.text = getString(R.string.options_count, prizes.size)
            binding.emptyState.visibility = if (prizes.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
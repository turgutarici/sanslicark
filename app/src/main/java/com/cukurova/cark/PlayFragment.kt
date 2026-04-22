package com.cukurova.cark

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cukurova.cark.databinding.FragmentPlayBinding
import java.util.Locale
import kotlin.random.Random

class PlayFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrizeViewModel by activityViewModels()
    private var tts: TextToSpeech? = null

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

        // Initialize TextToSpeech
        tts = TextToSpeech(requireContext(), this)

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
                speakResult(result)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("tr", "TR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
            }
        }
    }

    private fun speakResult(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tts?.stop()
        tts?.shutdown()
        _binding = null
    }
}